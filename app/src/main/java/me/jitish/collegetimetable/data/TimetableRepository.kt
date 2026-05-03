package me.jitish.collegetimetable.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import java.util.UUID

class TimetableRepository(context: Context) {

    private val gson = Gson()

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    fun loadStore(): TimetableStore {
        val storeJson = prefs.getString(KEY_TIMETABLE_STORE, null)
        if (storeJson != null) {
            return try {
                sanitizeStore(gson.fromJson(storeJson, TimetableStore::class.java) ?: TimetableStore())
            } catch (e: Exception) {
                e.printStackTrace()
                defaultStore()
            }
        }

        val migratedTimetable = loadLegacyTimetable()
        return sanitizeStore(
            TimetableStore(
                profiles = listOf(defaultProfile(migratedTimetable)),
                selectedPersonId = null
            )
        )
    }

    fun saveStore(store: TimetableStore) {
        prefs.edit {
            putString(KEY_TIMETABLE_STORE, gson.toJson(sanitizeStore(store)))
        }
    }

    private fun loadLegacyTimetable(): TimetableData {
        val timetableJson = prefs.getString(KEY_TIMETABLE, null) ?: return TimetableData()
        return try {
            sanitizeTimetable(gson.fromJson(timetableJson, TimetableData::class.java) ?: TimetableData())
        } catch (e: Exception) {
            e.printStackTrace()
            TimetableData()
        }
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_MODE, isDark) }
    }

    fun is24HourFormat(): Boolean {
        return prefs.getBoolean(KEY_24_HOUR_FORMAT, true)
    }

    fun set24HourFormat(is24Hour: Boolean) {
        prefs.edit { putBoolean(KEY_24_HOUR_FORMAT, is24Hour) }
    }

    private fun sanitizeStore(store: TimetableStore): TimetableStore {
        val sanitizedProfiles = store.profiles
            .mapNotNull { profile ->
                val person = runCatching { profile.person }.getOrNull() ?: return@mapNotNull null
                val personId: String = person.id
                val personName: String = person.name
                val cleanName = personName.trim()

                if (cleanName.isBlank()) return@mapNotNull null

                TimetableProfile(
                    person = Person(
                        id = if (personId.isNullOrBlank()) UUID.randomUUID().toString() else personId,
                        name = cleanName
                    ),
                    timetable = sanitizeTimetable(runCatching { profile.timetable }.getOrNull() ?: TimetableData())
                )
            }
            .distinctBy { it.person.id }
            .ifEmpty { listOf(defaultProfile()) }

        val selectedId = store.selectedPersonId
            ?.takeIf { id -> sanitizedProfiles.any { it.person.id == id } }
            ?: sanitizedProfiles.first().person.id

        return TimetableStore(
            profiles = sanitizedProfiles,
            selectedPersonId = selectedId
        )
    }

    private fun sanitizeTimetable(timetable: TimetableData): TimetableData {
        val sanitized = TIMETABLE_DAYS.mapNotNull { day ->
            val classMap = runCatching { timetable.timetable }.getOrNull().orEmpty()
            val classes = classMap[day]
                .orEmpty()
                .map { classInfo ->
                    val classId: String = classInfo.id
                    classInfo.copy(
                        slot = classInfo.slot.trim().uppercase(),
                        courseCode = classInfo.courseCode.trim(),
                        courseTitle = classInfo.courseTitle.trim(),
                        start = classInfo.start.trim(),
                        end = classInfo.end.trim(),
                        venue = classInfo.venue.trim(),
                        facultyName = classInfo.facultyName.trim(),
                        id = if (classId.isBlank()) UUID.randomUUID().toString() else classId
                    )
                }
                .filter { it.courseTitle.isNotBlank() && it.start.isNotBlank() && it.end.isNotBlank() }
                .sortedBy { it.start }

            if (classes.isEmpty()) null else day to classes
        }.toMap()

        return TimetableData(sanitized)
    }

    private fun defaultStore(): TimetableStore {
        val profile = defaultProfile()
        return TimetableStore(
            profiles = listOf(profile),
            selectedPersonId = profile.person.id
        )
    }

    private fun defaultProfile(timetable: TimetableData = TimetableData()): TimetableProfile {
        return TimetableProfile(
            person = Person(name = "My Timetable"),
            timetable = sanitizeTimetable(timetable)
        )
    }

    companion object {
        private const val PREFS_NAME = "timetable_prefs"
        private const val KEY_TIMETABLE_STORE = "user_timetable_store"
        private const val KEY_TIMETABLE = "user_timetable"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_24_HOUR_FORMAT = "is_24_hour_format"
    }
}
