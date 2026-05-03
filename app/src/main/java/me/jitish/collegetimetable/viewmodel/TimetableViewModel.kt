package me.jitish.collegetimetable.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.data.ScheduledClassEntry
import me.jitish.collegetimetable.data.TIMETABLE_DAYS
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.data.TimetableProfile
import me.jitish.collegetimetable.data.TimetableRepository
import me.jitish.collegetimetable.data.TimetableStore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

data class ClassState(
    val currentClass: ClassInfo? = null,
    val upcomingClass: ClassInfo? = null,
    val remainingClasses: List<ClassInfo> = emptyList(), // Classes after upcoming
    val currentClassTimeRemaining: Long = 0L, // in seconds
    val upcomingClassStartsIn: Long = 0L, // in seconds
    val isClassRunning: Boolean = false
)

data class TimetableUiState(
    val profiles: List<TimetableProfile> = emptyList(),
    val selectedPersonId: String? = null,
    val timetable: TimetableData = TimetableData(),
    val classState: ClassState = ClassState(),
    val isDarkMode: Boolean = false,
    val is24HourFormat: Boolean = true,
    val isLoading: Boolean = true
) {
    val people: List<Person>
        get() = profiles.map { it.person }

    val selectedPerson: Person?
        get() = profiles.firstOrNull { it.person.id == selectedPersonId }?.person

    val hasTimetableEntries: Boolean
        get() = timetable.hasEntries
}

class TimetableViewModel(private val repository: TimetableRepository) : ViewModel() {

    var uiState = MutableStateFlow(TimetableUiState())
        private set

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        loadStore()
        startTimer()
    }

    private fun loadStore() {
        viewModelScope.launch {
            val store = repository.loadStore()
            val savedDarkMode = repository.isDarkMode()
            val saved24HourFormat = repository.is24HourFormat()
            uiState.value = uiState.value.copy(
                profiles = store.profiles,
                selectedPersonId = store.selectedPersonId,
                timetable = selectedTimetable(store),
                isLoading = false,
                isDarkMode = savedDarkMode,
                is24HourFormat = saved24HourFormat
            )
            updateClassState()
        }
    }

    fun selectPerson(person: Person) {
        saveStore(currentStore().copy(selectedPersonId = person.id))
    }

    fun addPerson(name: String) {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return

        val person = Person(name = cleanName)
        val profile = TimetableProfile(person = person)
        val store = currentStore()

        saveStore(
            store.copy(
                profiles = store.profiles + profile,
                selectedPersonId = person.id
            )
        )
    }

    fun renamePerson(personId: String, name: String) {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return

        val store = currentStore()
        saveStore(
            store.copy(
                profiles = store.profiles.map { profile ->
                    if (profile.person.id == personId) {
                        profile.copy(person = profile.person.copy(name = cleanName))
                    } else {
                        profile
                    }
                }
            )
        )
    }

    fun deletePerson(personId: String) {
        val store = currentStore()
        val remainingProfiles = store.profiles.filterNot { it.person.id == personId }
        if (remainingProfiles.isEmpty()) return

        val selectedId = store.selectedPersonId
            .takeIf { it != personId }
            ?: remainingProfiles.first().person.id

        saveStore(
            store.copy(
                profiles = remainingProfiles,
                selectedPersonId = selectedId
            )
        )
    }

    fun addClasses(entries: List<ScheduledClassEntry>) {
        if (entries.isEmpty()) return

        val timetableMap = uiState.value.timetable.timetable.toMutableMap()
        addEntriesToTimetable(timetableMap, entries)
        saveSelectedTimetable(TimetableData(timetableMap))
    }

    fun updateClassSchedule(
        @Suppress("UNUSED_PARAMETER") originalDay: String,
        originalClassId: String,
        entries: List<ScheduledClassEntry>
    ) {
        if (entries.isEmpty()) return

        val timetableMap = uiState.value.timetable.timetable.toMutableMap()
        TIMETABLE_DAYS.forEach { day ->
            timetableMap[day] = timetableMap[day]
                .orEmpty()
                .filterNot { it.id == originalClassId }
        }
        addEntriesToTimetable(timetableMap, entries)
        saveSelectedTimetable(TimetableData(timetableMap))
    }


    fun deleteClass(day: String, classId: String) {
        val normalizedDay = normalizeDay(day)
        val timetableMap = uiState.value.timetable.timetable.toMutableMap()
        timetableMap[normalizedDay] = timetableMap[normalizedDay]
            .orEmpty()
            .filterNot { it.id == classId }

        saveSelectedTimetable(TimetableData(timetableMap))
    }

    fun toggleDarkMode() {
        val newDarkMode = !uiState.value.isDarkMode
        repository.setDarkMode(newDarkMode)
        uiState.value = uiState.value.copy(
            isDarkMode = newDarkMode
        )
    }

    fun toggle24HourFormat() {
        val new24HourFormat = !uiState.value.is24HourFormat
        repository.set24HourFormat(new24HourFormat)
        uiState.value = uiState.value.copy(
            is24HourFormat = new24HourFormat
        )
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // Update every second
                updateClassState()
            }
        }
    }

    private fun updateClassState() {
        val timetable = uiState.value.timetable

        if (!timetable.hasEntries) {
            uiState.value = uiState.value.copy(classState = ClassState())
            return
        }

        val currentDay = getCurrentDayString()
        val currentTime = LocalTime.now()

        val todayClasses = timetable.timetable[currentDay] ?: emptyList()

        var currentClass: ClassInfo? = null
        var upcomingClass: ClassInfo? = null
        val remainingClasses = mutableListOf<ClassInfo>()
        var currentClassTimeRemaining = 0L
        var upcomingClassStartsIn = 0L
        var isClassRunning = false
        var foundUpcoming = false

        val sortedClasses = todayClasses.sortedBy { parseTimeOrNull(it.start) ?: LocalTime.MAX }

        for (classInfo in sortedClasses) {
            val startTime = parseTimeOrNull(classInfo.start) ?: continue
            val endTime = parseTimeOrNull(classInfo.end) ?: continue

            when {
                // Class is currently running
                currentTime >= startTime && currentTime < endTime -> {
                    currentClass = classInfo
                    isClassRunning = true
                    currentClassTimeRemaining = ChronoUnit.SECONDS.between(currentTime, endTime)
                }
                // Class is upcoming (hasn't started yet)
                currentTime < startTime -> {
                    if (!foundUpcoming) {
                        upcomingClass = classInfo
                        upcomingClassStartsIn = ChronoUnit.SECONDS.between(currentTime, startTime)
                        foundUpcoming = true
                    } else {
                        remainingClasses.add(classInfo)
                    }
                }
            }
        }

        // If no current class but there's a running class state, find the next class
        if (currentClass == null && upcomingClass == null) {
            // Check if all classes for today are over - look for tomorrow's first class
            val nextDayClasses = getNextDayClasses(timetable, currentDay)
            if (nextDayClasses.isNotEmpty()) {
                upcomingClass = nextDayClasses.first()
                // Calculate time until next day's class (simplified - shows as next day)
                upcomingClassStartsIn = -1 // Indicates "tomorrow"
            }
        }

        uiState.value = uiState.value.copy(
            classState = ClassState(
                currentClass = currentClass,
                upcomingClass = upcomingClass,
                remainingClasses = remainingClasses,
                currentClassTimeRemaining = currentClassTimeRemaining,
                upcomingClassStartsIn = upcomingClassStartsIn,
                isClassRunning = isClassRunning
            )
        )
    }

    private fun getCurrentDayString(): String {
        return when (LocalDate.now().dayOfWeek) {
            DayOfWeek.MONDAY -> "MONDAY"
            DayOfWeek.TUESDAY -> "TUESDAY"
            DayOfWeek.WEDNESDAY -> "WEDNESDAY"
            DayOfWeek.THURSDAY -> "THURSDAY"
            DayOfWeek.FRIDAY -> "FRIDAY"
            DayOfWeek.SATURDAY -> "SATURDAY"
            DayOfWeek.SUNDAY -> "SUNDAY"
        }
    }

    private fun getNextDayClasses(timetable: TimetableData, currentDay: String): List<ClassInfo> {
        val currentIndex = TIMETABLE_DAYS.indexOf(currentDay)

        // Check subsequent days
        for (i in 1..7) {
            val nextDayIndex = (currentIndex + i) % TIMETABLE_DAYS.size
            val nextDay = TIMETABLE_DAYS[nextDayIndex]
            val classes = timetable.timetable[nextDay]
            if (!classes.isNullOrEmpty()) {
                return classes.sortedBy { parseTimeOrNull(it.start) ?: LocalTime.MAX }
            }
        }
        return emptyList()
    }

    private fun saveSelectedTimetable(timetable: TimetableData) {
        val sanitized = sanitizeTimetable(timetable)
        val selectedPersonId = uiState.value.selectedPersonId ?: return
        val store = currentStore().copy(
            profiles = uiState.value.profiles.map { profile ->
                if (profile.person.id == selectedPersonId) {
                    profile.copy(timetable = sanitized)
                } else {
                    profile
                }
            }
        )

        saveStore(store)
    }

    private fun addEntriesToTimetable(
        timetableMap: MutableMap<String, List<ClassInfo>>,
        entries: List<ScheduledClassEntry>
    ) {
        entries.forEach { entry ->
            val day = normalizeDay(entry.day)
            timetableMap[day] = timetableMap[day].orEmpty() + entry.classInfo.withStableId()
        }
    }

    private fun saveStore(store: TimetableStore) {
        val normalizedStore = normalizeStore(store)
        repository.saveStore(normalizedStore)
        uiState.value = uiState.value.copy(
            profiles = normalizedStore.profiles,
            selectedPersonId = normalizedStore.selectedPersonId,
            timetable = selectedTimetable(normalizedStore)
        )
        updateClassState()
    }

    private fun currentStore(): TimetableStore {
        return TimetableStore(
            profiles = uiState.value.profiles,
            selectedPersonId = uiState.value.selectedPersonId
        )
    }

    private fun selectedTimetable(store: TimetableStore): TimetableData {
        return store.profiles
            .firstOrNull { it.person.id == store.selectedPersonId }
            ?.timetable
            ?: store.profiles.firstOrNull()?.timetable
            ?: TimetableData()
    }

    private fun normalizeStore(store: TimetableStore): TimetableStore {
        val profiles = store.profiles.ifEmpty {
            listOf(TimetableProfile(person = Person(name = "My Timetable")))
        }
        val selectedId = store.selectedPersonId
            ?.takeIf { id -> profiles.any { it.person.id == id } }
            ?: profiles.first().person.id

        return TimetableStore(
            profiles = profiles.map { profile ->
                profile.copy(
                    timetable = sanitizeTimetable(
                        runCatching { profile.timetable }.getOrNull() ?: TimetableData()
                    )
                )
            },
            selectedPersonId = selectedId
        )
    }

    private fun sanitizeTimetable(timetable: TimetableData): TimetableData {
        val sanitizedMap = TIMETABLE_DAYS.mapNotNull { day ->
            val classes = timetable.timetable[day]
                .orEmpty()
                .map { classInfo ->
                    classInfo.copy(
                        slot = classInfo.slot.trim().uppercase(),
                        courseCode = classInfo.courseCode.trim(),
                        courseTitle = classInfo.courseTitle.trim(),
                        start = classInfo.start.trim(),
                        end = classInfo.end.trim(),
                        venue = classInfo.venue.trim(),
                        facultyName = classInfo.facultyName.trim()
                    )
                }
                .filter { classInfo ->
                    classInfo.courseTitle.isNotBlank() &&
                        parseTimeOrNull(classInfo.start) != null &&
                        parseTimeOrNull(classInfo.end) != null
                }
                .map { it.withStableId() }
                .sortedBy { parseTimeOrNull(it.start) ?: LocalTime.MAX }

            if (classes.isEmpty()) null else day to classes
        }.toMap()

        return TimetableData(sanitizedMap)
    }

    private fun ClassInfo.withStableId(): ClassInfo {
        val classId: String = id
        return if (classId.isBlank()) copy(id = UUID.randomUUID().toString()) else this
    }

    private fun normalizeDay(day: String): String {
        return day.uppercase().takeIf { it in TIMETABLE_DAYS } ?: TIMETABLE_DAYS.first()
    }

    private fun parseTimeOrNull(timeString: String): LocalTime? {
        return try {
            LocalTime.parse(timeString, timeFormatter)
        } catch (_: Exception) {
            null
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
                return TimetableViewModel(TimetableRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
