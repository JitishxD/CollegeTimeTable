package me.jitish.collegetimetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.ScheduledClassEntry
import me.jitish.collegetimetable.data.TIMETABLE_DAYS
import me.jitish.collegetimetable.utils.COLLEGE_SLOTS
import me.jitish.collegetimetable.utils.buildScheduledClassEntries
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private enum class ScheduleEntryMode {
    College,
    Normal
}

private val ManualTimeParser = DateTimeFormatter.ofPattern("H:mm")
private val ManualTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun TimetableEntryDialog(
    initialDay: String,
    initialClassInfo: ClassInfo?,
    initialSlotExpression: String = initialClassInfo?.slot.orEmpty(),
    onDismiss: () -> Unit,
    onSave: (List<ScheduledClassEntry>) -> Unit
) {
    val inferredMode = remember(initialDay, initialClassInfo?.id, initialSlotExpression) {
        when {
            initialClassInfo?.isCollegeMappedEntry() == false -> ScheduleEntryMode.Normal
            else -> ScheduleEntryMode.College
        }
    }
    var entryMode by remember(initialDay, initialClassInfo?.id, initialSlotExpression) {
        mutableStateOf(inferredMode)
    }
    var slotExpression by remember(initialDay, initialClassInfo?.id, initialSlotExpression) {
        mutableStateOf(initialSlotExpression)
    }
    var manualDay by remember(initialDay, initialClassInfo?.id) {
        mutableStateOf(initialDay.takeIf { it in TIMETABLE_DAYS } ?: TIMETABLE_DAYS.first())
    }
    var manualSlot by remember(initialClassInfo?.id, initialSlotExpression) {
        mutableStateOf(initialClassInfo?.slot ?: initialSlotExpression)
    }
    var startTime by remember(initialClassInfo?.id) { mutableStateOf(initialClassInfo?.start.orEmpty()) }
    var endTime by remember(initialClassInfo?.id) { mutableStateOf(initialClassInfo?.end.orEmpty()) }
    var courseCode by remember(initialClassInfo?.id) { mutableStateOf(initialClassInfo?.courseCode.orEmpty()) }
    var courseTitle by remember(initialClassInfo?.id) { mutableStateOf(initialClassInfo?.courseTitle.orEmpty()) }
    var venue by remember(initialClassInfo?.id) { mutableStateOf(initialClassInfo?.venue.orEmpty()) }
    var facultyName by remember(initialClassInfo?.id) { mutableStateOf(initialClassInfo?.facultyName.orEmpty()) }
    val newManualEntryId = remember(initialClassInfo?.id) { UUID.randomUUID().toString() }

    val slotExpansion = buildScheduledClassEntries(
        slotExpression = slotExpression,
        courseCode = courseCode,
        courseTitle = courseTitle,
        venue = venue,
        facultyName = facultyName
    )
    val expandedEntries = if (initialClassInfo != null && slotExpansion.entries.size == 1) {
        listOf(
            slotExpansion.entries.first().copy(
                classInfo = slotExpansion.entries.first().classInfo.copy(id = initialClassInfo.id)
            )
        )
    } else {
        slotExpansion.entries
    }
    val startLocalTime = parseManualTime(startTime)
    val endLocalTime = parseManualTime(endTime)
    val hasValidManualTimes = startLocalTime != null &&
        endLocalTime != null &&
        endLocalTime.isAfter(startLocalTime)
    val manualEntries = listOf(
        ScheduledClassEntry(
            day = manualDay,
            classInfo = ClassInfo(
                slot = manualSlot.trim().uppercase(),
                courseCode = courseCode.trim(),
                courseTitle = courseTitle.trim(),
                start = startLocalTime?.format(ManualTimeFormatter) ?: startTime.trim(),
                end = endLocalTime?.format(ManualTimeFormatter) ?: endTime.trim(),
                venue = venue.trim(),
                facultyName = facultyName.trim(),
                id = initialClassInfo?.id ?: newManualEntryId
            )
        )
    )
    val entriesToSave = when (entryMode) {
        ScheduleEntryMode.College -> expandedEntries
        ScheduleEntryMode.Normal -> manualEntries
    }
    val hasRequiredSharedFields = courseTitle.isNotBlank() && venue.isNotBlank()
    val canSave = when (entryMode) {
        ScheduleEntryMode.College -> expandedEntries.isNotEmpty() &&
            slotExpansion.invalidTokens.isEmpty() &&
            hasRequiredSharedFields

        ScheduleEntryMode.Normal -> manualDay in TIMETABLE_DAYS &&
            manualSlot.isNotBlank() &&
            hasValidManualTimes &&
            hasRequiredSharedFields
    }
    val shouldShowPreview = when (entryMode) {
        ScheduleEntryMode.College -> slotExpression.isNotBlank() && expandedEntries.isNotEmpty()
        ScheduleEntryMode.Normal -> manualSlot.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialClassInfo == null) "Add schedule entry" else "Edit schedule entry")
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ScheduleEntryModeToggle(
                    selectedMode = entryMode,
                    onModeSelected = { entryMode = it }
                )

                if (entryMode == ScheduleEntryMode.College) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = slotExpression,
                            onValueChange = { slotExpression = it.uppercase() },
                            label = { Text("Slots") },
                            placeholder = { Text("A11+A12+A13") },
                            singleLine = true,
                            isError = slotExpansion.invalidTokens.isNotEmpty(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters
                            ),
                            supportingText = {
                                if (slotExpansion.invalidTokens.isNotEmpty()) {
                                    Text("Unknown: ${slotExpansion.invalidTokens.joinToString(", ")}")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CourseCodeField(
                            value = courseCode,
                            onValueChange = { courseCode = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    NormalScheduleFields(
                        day = manualDay,
                        onDayChange = { manualDay = it },
                        slot = manualSlot,
                        onSlotChange = { manualSlot = it },
                        start = startTime,
                        onStartChange = { startTime = it },
                        end = endTime,
                        onEndChange = { endTime = it },
                        courseCode = courseCode,
                        onCourseCodeChange = { courseCode = it },
                        isStartError = startTime.isNotBlank() && startLocalTime == null,
                        isEndError = endTime.isNotBlank() && (endLocalTime == null || !hasValidManualTimes)
                    )
                }

                OutlinedTextField(
                    value = courseTitle,
                    onValueChange = { courseTitle = it },
                    label = { Text("Course title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = facultyName,
                    onValueChange = { facultyName = it },
                    label = { Text("Faculty (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    placeholder = { Text("AB1-123, AB2-123, LC-120") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (shouldShowPreview) {
                    Text(
                        text = entriesToSave.joinToString { entry ->
                            "${entry.classInfo.slot} ${entry.day.toDisplayDay()} ${entry.classInfo.start}-${entry.classInfo.end}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(entriesToSave)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    )
}

private fun parseManualTime(value: String): LocalTime? {
    return try {
        LocalTime.parse(value.trim().replace(".", ":"), ManualTimeParser)
    } catch (_: Exception) {
        null
    }
}

private fun ClassInfo.isCollegeMappedEntry(): Boolean {
    val collegeSlot = COLLEGE_SLOTS[slot.uppercase()] ?: return false
    return start == collegeSlot.start && end == collegeSlot.end
}

@Composable
private fun ScheduleEntryModeToggle(
    selectedMode: ScheduleEntryMode,
    onModeSelected: (ScheduleEntryMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScheduleEntryMode.entries.forEach { mode ->
            val modifier = Modifier.weight(1f)
            val label = when (mode) {
                ScheduleEntryMode.College -> "College slots"
                ScheduleEntryMode.Normal -> "Normal"
            }

            if (mode == selectedMode) {
                Button(
                    onClick = { onModeSelected(mode) },
                    modifier = modifier,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(label)
                }
            } else {
                OutlinedButton(
                    onClick = { onModeSelected(mode) },
                    modifier = modifier,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun NormalScheduleFields(
    day: String,
    onDayChange: (String) -> Unit,
    slot: String,
    onSlotChange: (String) -> Unit,
    start: String,
    onStartChange: (String) -> Unit,
    end: String,
    onEndChange: (String) -> Unit,
    courseCode: String,
    onCourseCodeChange: (String) -> Unit,
    isStartError: Boolean,
    isEndError: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DayDropdown(
                selectedDay = day,
                onDaySelected = onDayChange,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = slot,
                onValueChange = { onSlotChange(it.uppercase()) },
                label = { Text("Slot") },
                placeholder = { Text("Custom") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = start,
                onValueChange = onStartChange,
                label = { Text("Start") },
                placeholder = { Text("08:30") },
                singleLine = true,
                isError = isStartError,
                supportingText = {
                    if (isStartError) Text("Use HH:mm")
                },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = end,
                onValueChange = onEndChange,
                label = { Text("End") },
                placeholder = { Text("10:00") },
                singleLine = true,
                isError = isEndError,
                supportingText = {
                    if (isEndError) Text("After start")
                },
                modifier = Modifier.weight(1f)
            )
        }

        CourseCodeField(
            value = courseCode,
            onValueChange = onCourseCodeChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDropdown(
    selectedDay: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedDay.toDisplayDay(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Day") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TIMETABLE_DAYS.forEach { day ->
                DropdownMenuItem(
                    text = { Text(day.toDisplayDay()) },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CourseCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.uppercase()) },
        label = { Text("Code (optional)") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters
        ),
        modifier = modifier
    )
}

@Composable
fun ConfirmDeleteEntryDialog(
    day: String,
    classInfo: ClassInfo,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete entry?") },
        text = {
            Text("${classInfo.courseTitle} will be removed from ${day.toDisplayDay()}.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    )
}
