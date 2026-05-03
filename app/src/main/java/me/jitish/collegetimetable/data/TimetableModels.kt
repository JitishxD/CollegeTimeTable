package me.jitish.collegetimetable.data

import java.util.UUID

val TIMETABLE_DAYS = listOf(
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY"
)

data class TimetableData(
    val timetable: Map<String, List<ClassInfo>> = emptyMap()
) {
    val hasEntries: Boolean
        get() = timetable.values.any { it.isNotEmpty() }
}

data class Person(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)

data class TimetableProfile(
    val person: Person,
    val timetable: TimetableData = TimetableData()
)

data class TimetableStore(
    val profiles: List<TimetableProfile> = emptyList(),
    val selectedPersonId: String? = null
)

data class ScheduledClassEntry(
    val day: String,
    val classInfo: ClassInfo
)

data class ClassInfo(
    val slot: String,
    val courseCode: String,
    val courseTitle: String,
    val start: String,
    val end: String,
    val venue: String,
    val facultyName: String = "",
    val id: String = UUID.randomUUID().toString()
)
