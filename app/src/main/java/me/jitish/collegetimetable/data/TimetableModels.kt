package me.jitish.collegetimetable.data

data class TimetableData(
    val timetable: Map<String, List<ClassInfo>>
)

data class ClassInfo(
    val slot: String,
    val courseCode: String,
    val courseTitle: String,
    val start: String,
    val end: String,
    val venue: String
)

data class Person(
    val name: String,
    val fileName: String
)
