package me.jitish.collegetimetable.utils

import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.ScheduledClassEntry
import java.util.Locale
import java.util.UUID

data class CollegeSlot(
    val code: String,
    val day: String,
    val start: String,
    val end: String
)

data class CollegeSlotColumn(
    val label: String,
    val start: String,
    val end: String
)

data class CollegeSlotGridRow(
    val day: String,
    val label: String,
    val slotCodes: List<String>
)

data class SlotParseResult(
    val slots: List<CollegeSlot>,
    val invalidTokens: List<String>
) {
    val isValid: Boolean
        get() = slots.isNotEmpty() && invalidTokens.isEmpty()
}

data class SlotExpansionResult(
    val entries: List<ScheduledClassEntry>,
    val invalidTokens: List<String>
)

val COLLEGE_SLOT_COLUMNS = listOf(
    CollegeSlotColumn("8.30-10.00", "08:30", "10:00"),
    CollegeSlotColumn("10.05-11.35", "10:05", "11:35"),
    CollegeSlotColumn("11.40-13.10", "11:40", "13:10"),
    CollegeSlotColumn("13.15-14.45", "13:15", "14:45"),
    CollegeSlotColumn("14.50-16.20", "14:50", "16:20"),
    CollegeSlotColumn("16.25-17.55", "16:25", "17:55"),
    CollegeSlotColumn("18.00-19.30", "18:00", "19:30")
)

val COLLEGE_SLOT_GRID = listOf(
    CollegeSlotGridRow("MONDAY", "Mon", listOf("A11", "B11", "C11", "A21", "A14", "B21", "C21")),
    CollegeSlotGridRow("TUESDAY", "Tue", listOf("D11", "E11", "F11", "D21", "E14", "E21", "F21")),
    CollegeSlotGridRow("WEDNESDAY", "Wed", listOf("A12", "B12", "C12", "A22", "B14", "B22", "A24")),
    CollegeSlotGridRow("THURSDAY", "Thu", listOf("D12", "E12", "F12", "D22", "F14", "E22", "F22")),
    CollegeSlotGridRow("FRIDAY", "Fri", listOf("A13", "B13", "C13", "A23", "C14", "B23", "B24")),
    CollegeSlotGridRow("SATURDAY", "Sat", listOf("D13", "E13", "F13", "D23", "D14", "D24", "E23"))
)

val COLLEGE_SLOTS: Map<String, CollegeSlot> = listOf(
    CollegeSlot("A11", "MONDAY", "08:30", "10:00"),
    CollegeSlot("B11", "MONDAY", "10:05", "11:35"),
    CollegeSlot("C11", "MONDAY", "11:40", "13:10"),
    CollegeSlot("A21", "MONDAY", "13:15", "14:45"),
    CollegeSlot("A14", "MONDAY", "14:50", "16:20"),
    CollegeSlot("B21", "MONDAY", "16:25", "17:55"),
    CollegeSlot("C21", "MONDAY", "18:00", "19:30"),

    CollegeSlot("D11", "TUESDAY", "08:30", "10:00"),
    CollegeSlot("E11", "TUESDAY", "10:05", "11:35"),
    CollegeSlot("F11", "TUESDAY", "11:40", "13:10"),
    CollegeSlot("D21", "TUESDAY", "13:15", "14:45"),
    CollegeSlot("E14", "TUESDAY", "14:50", "16:20"),
    CollegeSlot("E21", "TUESDAY", "16:25", "17:55"),
    CollegeSlot("F21", "TUESDAY", "18:00", "19:30"),

    CollegeSlot("A12", "WEDNESDAY", "08:30", "10:00"),
    CollegeSlot("B12", "WEDNESDAY", "10:05", "11:35"),
    CollegeSlot("C12", "WEDNESDAY", "11:40", "13:10"),
    CollegeSlot("A22", "WEDNESDAY", "13:15", "14:45"),
    CollegeSlot("B14", "WEDNESDAY", "14:50", "16:20"),
    CollegeSlot("B22", "WEDNESDAY", "16:25", "17:55"),
    CollegeSlot("A24", "WEDNESDAY", "18:00", "19:30"),

    CollegeSlot("D12", "THURSDAY", "08:30", "10:00"),
    CollegeSlot("E12", "THURSDAY", "10:05", "11:35"),
    CollegeSlot("F12", "THURSDAY", "11:40", "13:10"),
    CollegeSlot("D22", "THURSDAY", "13:15", "14:45"),
    CollegeSlot("F14", "THURSDAY", "14:50", "16:20"),
    CollegeSlot("E22", "THURSDAY", "16:25", "17:55"),
    CollegeSlot("F22", "THURSDAY", "18:00", "19:30"),

    CollegeSlot("A13", "FRIDAY", "08:30", "10:00"),
    CollegeSlot("B13", "FRIDAY", "10:05", "11:35"),
    CollegeSlot("C13", "FRIDAY", "11:40", "13:10"),
    CollegeSlot("A23", "FRIDAY", "13:15", "14:45"),
    CollegeSlot("C14", "FRIDAY", "14:50", "16:20"),
    CollegeSlot("B23", "FRIDAY", "16:25", "17:55"),
    CollegeSlot("B24", "FRIDAY", "18:00", "19:30"),

    CollegeSlot("D13", "SATURDAY", "08:30", "10:00"),
    CollegeSlot("E13", "SATURDAY", "10:05", "11:35"),
    CollegeSlot("F13", "SATURDAY", "11:40", "13:10"),
    CollegeSlot("D23", "SATURDAY", "13:15", "14:45"),
    CollegeSlot("D14", "SATURDAY", "14:50", "16:20"),
    CollegeSlot("D24", "SATURDAY", "16:25", "17:55"),
    CollegeSlot("E23", "SATURDAY", "18:00", "19:30")
).associateBy { it.code }

fun formatTime(time: String, is24HourFormat: Boolean): String {
    if (is24HourFormat) return time

    return try {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1]
        val period = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        String.format(Locale.US, "%d:%s %s", hour12, minute, period)
    } catch (_: Exception) {
        time
    }
}

fun parseCollegeSlots(expression: String): SlotParseResult {
    val tokens = expression
        .uppercase()
        .split(Regex("[+,\n\r\t ]+"))
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()

    val slots = tokens.mapNotNull { COLLEGE_SLOTS[it] }
    val invalidTokens = tokens.filterNot { COLLEGE_SLOTS.containsKey(it) }

    return SlotParseResult(slots = slots, invalidTokens = invalidTokens)
}

fun buildScheduledClassEntries(
    slotExpression: String,
    courseCode: String,
    courseTitle: String,
    venue: String,
    facultyName: String = ""
): SlotExpansionResult {
    val parseResult = parseCollegeSlots(slotExpression)
    if (!parseResult.isValid) {
        return SlotExpansionResult(
            entries = emptyList(),
            invalidTokens = parseResult.invalidTokens
        )
    }

    val entries = parseResult.slots.map { slot ->
        ScheduledClassEntry(
            day = slot.day,
            classInfo = ClassInfo(
                slot = slot.code,
                courseCode = courseCode.trim(),
                courseTitle = courseTitle.trim(),
                start = slot.start,
                end = slot.end,
                venue = venue.trim(),
                facultyName = facultyName.trim(),
                id = UUID.randomUUID().toString()
            )
        )
    }

    return SlotExpansionResult(
        entries = entries,
        invalidTokens = emptyList()
    )
}
