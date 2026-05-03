package me.jitish.collegetimetable.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.ScheduledClassEntry
import me.jitish.collegetimetable.data.TIMETABLE_DAYS
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.ui.components.CollegeTimetableGrid
import me.jitish.collegetimetable.ui.components.ConfirmDeleteEntryDialog
import me.jitish.collegetimetable.ui.components.DayScheduleCard
import me.jitish.collegetimetable.ui.components.TimetableEntryDialog
import me.jitish.collegetimetable.utils.COLLEGE_SLOTS

private data class EntryEditorState(
    val day: String,
    val classInfo: ClassInfo? = null,
    val slotExpression: String = classInfo?.slot.orEmpty()
)

private enum class TimetableViewMode {
    List,
    Grid
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullTimetableScreen(
    personName: String,
    timetable: TimetableData,
    isDarkMode: Boolean,
    onBackClick: () -> Unit,
    onAddClasses: (List<ScheduledClassEntry>) -> Unit,
    onUpdateClassSchedule: (String, String, List<ScheduledClassEntry>) -> Unit,
    onDeleteClass: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val verticalScrollState = rememberScrollState()
    var editorState by remember { mutableStateOf<EntryEditorState?>(null) }
    var deleteState by remember { mutableStateOf<EntryEditorState?>(null) }
    var viewMode by remember { mutableStateOf(TimetableViewMode.List) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (personName.isBlank()) "Manage Timetable" else "$personName's Timetable",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Complete Weekly Schedule",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editorState = EntryEditorState(TIMETABLE_DAYS.first()) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add schedule entry"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(verticalScrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimetableViewModeToggle(
                selectedMode = viewMode,
                onModeSelected = { viewMode = it }
            )

            if (viewMode == TimetableViewMode.List) {
                TIMETABLE_DAYS.forEach { day ->
                    DayScheduleCard(
                        day = day,
                        classes = timetable.timetable[day].orEmpty(),
                        isDarkMode = isDarkMode,
                        is24HourFormat = is24HourFormat,
                        onEditClass = { classInfo ->
                            editorState = EntryEditorState(day, classInfo)
                        },
                        onDeleteClass = { classInfo ->
                            deleteState = EntryEditorState(day, classInfo)
                        }
                    )
                }
            } else {
                CollegeTimetableGrid(
                    timetable = timetable,
                    isDarkMode = isDarkMode,
                    onSlotClick = { slotCode, classInfo ->
                        editorState = EntryEditorState(
                            day = COLLEGE_SLOTS[slotCode]?.day ?: TIMETABLE_DAYS.first(),
                            classInfo = classInfo,
                            slotExpression = classInfo?.slot ?: slotCode
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    editorState?.let { state ->
        TimetableEntryDialog(
            initialDay = state.day,
            initialClassInfo = state.classInfo,
            initialSlotExpression = state.slotExpression,
            onDismiss = { editorState = null },
            onSave = { entries ->
                val classInfo = state.classInfo
                if (classInfo == null) {
                    onAddClasses(entries)
                } else {
                    onUpdateClassSchedule(state.day, classInfo.id, entries)
                }
                editorState = null
            }
        )
    }

    deleteState?.classInfo?.let { classInfo ->
        ConfirmDeleteEntryDialog(
            day = deleteState?.day.orEmpty(),
            classInfo = classInfo,
            onDismiss = { deleteState = null },
            onConfirm = {
                onDeleteClass(deleteState?.day.orEmpty(), classInfo.id)
                deleteState = null
            }
        )
    }
}

@Composable
private fun TimetableViewModeToggle(
    selectedMode: TimetableViewMode,
    onModeSelected: (TimetableViewMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimetableViewMode.entries.forEach { mode ->
            val modifier = Modifier.weight(1f)
            if (mode == selectedMode) {
                Button(
                    onClick = { onModeSelected(mode) },
                    modifier = modifier,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(mode.name)
                }
            } else {
                OutlinedButton(
                    onClick = { onModeSelected(mode) },
                    modifier = modifier,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(mode.name)
                }
            }
        }
    }
}
