package me.jitish.collegetimetable.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.data.TimetableRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class ClassState(
    val currentClass: ClassInfo? = null,
    val upcomingClass: ClassInfo? = null,
    val remainingClasses: List<ClassInfo> = emptyList(), // Classes after upcoming
    val currentClassTimeRemaining: Long = 0L, // in seconds
    val upcomingClassStartsIn: Long = 0L, // in seconds
    val isClassRunning: Boolean = false
)

data class TimetableUiState(
    val people: List<Person> = emptyList(),
    val selectedPerson: Person? = null,
    val timetable: TimetableData? = null,
    val classState: ClassState = ClassState(),
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = true
)

class TimetableViewModel(private val repository: TimetableRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        loadPeople()
        startTimer()
    }

    private fun loadPeople() {
        viewModelScope.launch {
            val people = repository.getAvailablePeople()
            val savedDarkMode = repository.isDarkMode()
            _uiState.value = _uiState.value.copy(
                people = people,
                isLoading = false,
                isDarkMode = savedDarkMode
            )

            // Auto-select first person if available
            if (people.isNotEmpty()) {
                selectPerson(people.first())
            }
        }
    }

    fun selectPerson(person: Person) {
        viewModelScope.launch {
            val timetable = repository.loadTimetable(person.fileName)
            _uiState.value = _uiState.value.copy(
                selectedPerson = person,
                timetable = timetable
            )
            updateClassState()
        }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.isDarkMode
        repository.setDarkMode(newDarkMode)
        _uiState.value = _uiState.value.copy(
            isDarkMode = newDarkMode
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
        val timetable = _uiState.value.timetable ?: return

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

        // Sort classes by start time
        val sortedClasses = todayClasses.sortedBy { parseTime(it.start) }

        for (classInfo in sortedClasses) {
            val startTime = parseTime(classInfo.start)
            val endTime = parseTime(classInfo.end)

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

        _uiState.value = _uiState.value.copy(
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
        val daysOrder = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        val currentIndex = daysOrder.indexOf(currentDay)

        // Check subsequent days
        for (i in 1..7) {
            val nextDayIndex = (currentIndex + i) % 7
            val nextDay = daysOrder[nextDayIndex]
            val classes = timetable.timetable[nextDay]
            if (!classes.isNullOrEmpty()) {
                return classes.sortedBy { parseTime(it.start) }
            }
        }
        return emptyList()
    }

    private fun parseTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
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
