package me.jitish.collegetimetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.data.TimetableProfile
import me.jitish.collegetimetable.ui.screens.FullTimetableScreen
import me.jitish.collegetimetable.ui.screens.PeopleManagementScreen
import me.jitish.collegetimetable.ui.screens.TimetableScreen
import me.jitish.collegetimetable.ui.theme.AppGreen
import me.jitish.collegetimetable.ui.theme.AppGreenBright
import me.jitish.collegetimetable.ui.theme.CollegeTimeTableTheme
import me.jitish.collegetimetable.viewmodel.ClassState
import me.jitish.collegetimetable.viewmodel.TimetableUiState

private val previewPerson = Person(
    id = "jitish",
    name = "Jitish"
)

private val previewOtherPerson = Person(
    id = "utkarsh",
    name = "Utkarsh"
)

private val previewCurrentClass = ClassInfo(
    slot = "B11",
    courseCode = "ECE2002",
    courseTitle = "Digital Logic Design",
    start = "10:05",
    end = "11:35",
    venue = "AB1-414",
    facultyName = "Dr. Sharma"
)

private val previewUpcomingClass = ClassInfo(
    slot = "C11",
    courseCode = "",
    courseTitle = "Applied Linear Algebra",
    start = "11:40",
    end = "13:10",
    venue = "LC-120",
    facultyName = "Prof. Rao"
)

private val previewManualClass = ClassInfo(
    slot = "LAB-1",
    courseCode = "CSE2001",
    courseTitle = "Project Lab",
    start = "14:00",
    end = "15:30",
    venue = "AB2-204",
    facultyName = ""
)

private val previewMondayClasses = listOf(
    ClassInfo(
        slot = "A11",
        courseCode = "CSE1001",
        courseTitle = "Problem Solving and Programming",
        start = "08:30",
        end = "10:00",
        venue = "AB1-302",
        facultyName = "Dr. Iyer"
    ),
    previewCurrentClass,
    previewUpcomingClass
)

private val previewTuesdayClasses = listOf(
    ClassInfo(
        slot = "D11",
        courseCode = "PHY1001",
        courseTitle = "Engineering Physics",
        start = "08:30",
        end = "10:00",
        venue = "AB2-118",
        facultyName = "Dr. Menon"
    ),
    ClassInfo(
        slot = "E14",
        courseCode = "ENG1001",
        courseTitle = "Technical English",
        start = "14:50",
        end = "16:20",
        venue = "LC-205"
    )
)

private val previewWednesdayClasses = listOf(
    ClassInfo(
        slot = "A22",
        courseCode = "MAT3002",
        courseTitle = "Discrete Mathematics",
        start = "13:15",
        end = "14:45",
        venue = "AB1-123",
        facultyName = "Dr. Example"
    ),
    previewManualClass
)

private val previewTimetable = TimetableData(
    timetable = mapOf(
        "MONDAY" to previewMondayClasses,
        "TUESDAY" to previewTuesdayClasses,
        "WEDNESDAY" to previewWednesdayClasses
    )
)

private val previewProfiles = listOf(
    TimetableProfile(
        person = previewPerson,
        timetable = previewTimetable
    ),
    TimetableProfile(
        person = previewOtherPerson,
        timetable = TimetableData(
            mapOf("FRIDAY" to listOf(previewManualClass))
        )
    )
)

private val previewCurrentState = ClassState(
    currentClass = previewCurrentClass,
    upcomingClass = previewUpcomingClass,
    remainingClasses = listOf(previewManualClass),
    currentClassTimeRemaining = 1845,
    isClassRunning = true
)

private val previewUpcomingState = ClassState(
    upcomingClass = previewUpcomingClass,
    remainingClasses = listOf(previewManualClass),
    upcomingClassStartsIn = 900
)

@Preview(name = "Home - Current Class", showBackground = true, showSystemUi = true)
@Composable
private fun TimetableScreenCurrentPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = previewProfiles,
                selectedPersonId = previewPerson.id,
                timetable = previewTimetable,
                classState = previewCurrentState,
                isDarkMode = false,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {},
            onToggle24HourFormat = {},
            onManagePeople = {},
            onViewFullTimetable = {}
        )
    }
}

@Preview(name = "Home - Dark Current Class", showBackground = true, showSystemUi = true)
@Composable
private fun TimetableScreenCurrentDarkPreview() {
    CollegeTimeTableTheme(darkTheme = true) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = previewProfiles,
                selectedPersonId = previewPerson.id,
                timetable = previewTimetable,
                classState = previewCurrentState,
                isDarkMode = true,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {},
            onToggle24HourFormat = {},
            onManagePeople = {},
            onViewFullTimetable = {}
        )
    }
}

@Preview(name = "Home - Upcoming Class", showBackground = true, showSystemUi = true)
@Composable
private fun TimetableScreenUpcomingPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = previewProfiles,
                selectedPersonId = previewOtherPerson.id,
                timetable = previewTimetable,
                classState = previewUpcomingState,
                isDarkMode = false,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {},
            onToggle24HourFormat = {},
            onManagePeople = {},
            onViewFullTimetable = {}
        )
    }
}

@Preview(name = "Home - Empty", showBackground = true, showSystemUi = true)
@Composable
private fun TimetableScreenEmptyPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = previewProfiles,
                selectedPersonId = previewPerson.id,
                timetable = TimetableData(),
                isDarkMode = false,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {},
            onToggle24HourFormat = {},
            onManagePeople = {},
            onViewFullTimetable = {}
        )
    }
}

@Preview(name = "Home - Loading", showBackground = true, showSystemUi = true)
@Composable
private fun TimetableScreenLoadingPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = previewProfiles,
                selectedPersonId = previewPerson.id,
                isDarkMode = false,
                isLoading = true
            ),
            onPersonSelected = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(name = "Full Timetable - List", showBackground = true, showSystemUi = true)
@Composable
private fun FullTimetableScreenPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        FullTimetableScreen(
            personName = previewPerson.name,
            timetable = previewTimetable,
            isDarkMode = false,
            onBackClick = {},
            onAddClasses = {},
            onUpdateClassSchedule = { _, _, _ -> },
            onDeleteClass = { _, _ -> }
        )
    }
}

@Preview(name = "Full Timetable - Dark List", showBackground = true, showSystemUi = true)
@Composable
private fun FullTimetableScreenDarkPreview() {
    CollegeTimeTableTheme(darkTheme = true) {
        FullTimetableScreen(
            personName = previewPerson.name,
            timetable = previewTimetable,
            isDarkMode = true,
            onBackClick = {},
            onAddClasses = {},
            onUpdateClassSchedule = { _, _, _ -> },
            onDeleteClass = { _, _ -> }
        )
    }
}

@Preview(name = "People Management", showBackground = true, showSystemUi = true)
@Composable
private fun PeopleManagementScreenPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        PeopleManagementScreen(
            profiles = previewProfiles,
            selectedPersonId = previewPerson.id,
            onBackClick = {},
            onSelectPerson = {},
            onAddPerson = {},
            onRenamePerson = { _, _ -> },
            onDeletePerson = {}
        )
    }
}

@Preview(name = "People Management - Dark", showBackground = true, showSystemUi = true)
@Composable
private fun PeopleManagementScreenDarkPreview() {
    CollegeTimeTableTheme(darkTheme = true) {
        PeopleManagementScreen(
            profiles = previewProfiles,
            selectedPersonId = previewOtherPerson.id,
            onBackClick = {},
            onSelectPerson = {},
            onAddPerson = {},
            onRenamePerson = { _, _ -> },
            onDeletePerson = {}
        )
    }
}

@Preview(name = "College Grid", showBackground = true, widthDp = 430, heightDp = 620)
@Composable
private fun CollegeTimetableGridPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        CollegeTimetableGrid(
            timetable = previewTimetable,
            isDarkMode = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "College Grid - Dark", showBackground = true, widthDp = 430, heightDp = 620)
@Composable
private fun CollegeTimetableGridDarkPreview() {
    CollegeTimeTableTheme(darkTheme = true) {
        CollegeTimetableGrid(
            timetable = previewTimetable,
            isDarkMode = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Class Status - Current", showBackground = true)
@Composable
private fun ClassStatusCurrentPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        ClassStatusDisplay(
            classState = previewCurrentState,
            isDarkMode = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Day Schedule Card", showBackground = true)
@Composable
private fun DayScheduleCardPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        DayScheduleCard(
            day = "MONDAY",
            classes = previewMondayClasses,
            isDarkMode = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Class Item Row - Dark", showBackground = true, backgroundColor = 0xFF111318)
@Composable
private fun ClassItemRowDarkPreview() {
    CollegeTimeTableTheme(darkTheme = true) {
        ClassItemRow(
            classInfo = previewCurrentClass,
            isDarkMode = true,
            accentColor = AppGreenBright,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Empty Timetable Card", showBackground = true)
@Composable
private fun EmptyTimetableCardPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        EmptyTimetableCard(
            onManageClick = {},
            isDarkMode = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Action And Header Controls", showBackground = true)
@Composable
private fun ActionAndHeaderControlsPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsButton(onClick = {})
                ThemeToggleButton(isDarkMode = false, onToggle = {})
                ManagePeopleButton(onClick = {})
            }
            ViewFullTimetableButton(onClick = {}, isDarkMode = false)
        }
    }
}

@Preview(name = "Person Dropdown", showBackground = true)
@Composable
private fun PersonDropdownPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        PersonDropdown(
            people = previewProfiles.map { it.person },
            selectedPerson = previewPerson,
            onPersonSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Settings Dialog", showBackground = true)
@Composable
private fun SettingsDialogPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        SettingsDialog(
            showDialog = true,
            onDismiss = {},
            is24HourFormat = true,
            onToggle24HourFormat = {},
            isDarkMode = false
        )
    }
}

@Preview(name = "Timetable Entry - College Slots", showBackground = true)
@Composable
private fun TimetableEntryDialogCollegePreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableEntryDialog(
            initialDay = "MONDAY",
            initialClassInfo = null,
            initialSlotExpression = "A11+A12+A13",
            onDismiss = {},
            onSave = {}
        )
    }
}

@Preview(name = "Timetable Entry - Normal Edit", showBackground = true)
@Composable
private fun TimetableEntryDialogNormalPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableEntryDialog(
            initialDay = "WEDNESDAY",
            initialClassInfo = previewManualClass,
            initialSlotExpression = previewManualClass.slot,
            onDismiss = {},
            onSave = {}
        )
    }
}

@Preview(name = "Delete Entry Dialog", showBackground = true)
@Composable
private fun ConfirmDeleteEntryDialogPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        ConfirmDeleteEntryDialog(
            day = "MONDAY",
            classInfo = previewCurrentClass,
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Preview(name = "Person Name Dialog", showBackground = true)
@Composable
private fun PersonNameDialogPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        PersonNameDialog(
            title = "Add person",
            confirmLabel = "Add",
            initialName = "New Timetable",
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Preview(name = "Delete Person Dialog", showBackground = true)
@Composable
private fun ConfirmDeletePersonDialogPreview() {
    CollegeTimeTableTheme(darkTheme = false) {
        ConfirmDeletePersonDialog(
            person = previewOtherPerson,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
