package me.jitish.collegetimetable.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import java.io.InputStreamReader

class TimetableRepository(private val context: Context) {

    private val gson = Gson()

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    // List of known timetable files - add new people here
    private val knownTimetableFiles = listOf("Jitish.json", "Utkarsh.json", "Rahul.json", "Tanush.json")

    fun getAvailablePeople(): List<Person> {
        return knownTimetableFiles
            .filter { fileName ->
                // Verify the file actually exists in assets
                try {
                    context.assets.open(fileName).close()
                    true
                } catch (_: Exception) {
                    false
                }
            }
            .map { fileName ->
                Person(
                    name = fileName.removeSuffix(".json"),
                    fileName = fileName
                )
            }
    }

    fun loadTimetable(fileName: String): TimetableData? {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            gson.fromJson(reader, TimetableData::class.java).also {
                reader.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    companion object {
        private const val PREFS_NAME = "timetable_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_24_HOUR_FORMAT = "is_24_hour_format"
    }
}
