package me.jitish.collegetimetable.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.data.TimetableProfile
import me.jitish.collegetimetable.ui.screens.FullTimetableScreen
import me.jitish.collegetimetable.ui.screens.PeopleManagementScreen
import me.jitish.collegetimetable.ui.screens.TimetableScreen
import me.jitish.collegetimetable.ui.theme.CollegeTimeTableTheme
import me.jitish.collegetimetable.ui.theme.AppGreenBright
import me.jitish.collegetimetable.ui.theme.AppGreen
import me.jitish.collegetimetable.viewmodel.ClassState
import me.jitish.collegetimetable.viewmodel.TimetableUiState

private val samplePerson = Person(
    id = "jitish",
    name = "Jitish"
)

private val sampleOtherPerson = Person(
    id = "utkarsh",
    name = "Utkarsh"
)

private val sampleCurrentClass = ClassInfo(
    slot = "B11",
    courseCode = "ECE2002",
    courseTitle = "Digital Logic Design",
    start = "10:05",
    end = "11:35",
    venue = "AB-414"
)

private val sampleUpcomingClass = ClassInfo(
    slot = "C11",
    courseCode = "MAT3002",
    courseTitle = "Applied Linear Algebra",
    start = "11:40",
    end = "13:10",
    venue = "AB-126"
)

private val sampleRemainingClasses = listOf(
    ClassInfo(
        slot = "D11",
        courseCode = "CSE1001",
        courseTitle = "Problem Solving",
        start = "14:00",
        end = "15:30",
        venue = "AB-302"
    ),
    ClassInfo(
        slot = "E11",
        courseCode = "PHY1001",
        courseTitle = "Engineering Physics",
        start = "16:00",
        end = "17:30",
        venue = "AB-118"
    )
)

private val sampleMondayClasses = listOf(
    ClassInfo(
        slot = "A11",
        courseCode = "CSE1001",
        courseTitle = "Problem Solving and Programming",
        start = "08:00",
        end = "09:30",
        venue = "AB-302"
    ),
    sampleCurrentClass,
    sampleUpcomingClass
)

private val sampleTuesdayClasses = listOf(
    ClassInfo(
        slot = "D11",
        courseCode = "PHY1001",
        courseTitle = "Engineering Physics",
        start = "14:00",
        end = "15:30",
        venue = "AB-118"
    ),
    ClassInfo(
        slot = "E11",
        courseCode = "ENG1001",
        courseTitle = "Technical English",
        start = "16:00",
        end = "17:30",
        venue = "AB-205"
    )
)

private val sampleWednesdayClasses = listOf(
    ClassInfo(
        slot = "F11",
        courseCode = "CHE1001",
        courseTitle = "Engineering Chemistry",
        start = "09:00",
        end = "10:30",
        venue = "AB-310"
    )
)

private val sampleTimetable = TimetableData(
    timetable = mapOf(
        "MONDAY" to sampleMondayClasses,
        "TUESDAY" to sampleTuesdayClasses,
        "WEDNESDAY" to sampleWednesdayClasses
    )
)

private val sampleProfiles = listOf(
    TimetableProfile(
        person = samplePerson,
        timetable = sampleTimetable
    ),
    TimetableProfile(
        person = sampleOtherPerson,
        timetable = TimetableData(
            mapOf("FRIDAY" to sampleRemainingClasses)
        )
    )
)

@Preview(
    name = "Light Mode - Current Class Running",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewLightCurrentClass() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = sampleProfiles,
                selectedPersonId = samplePerson.id,
                timetable = sampleTimetable,
                classState = ClassState(
                    currentClass = sampleCurrentClass,
                    upcomingClass = sampleUpcomingClass,
                    remainingClasses = sampleRemainingClasses,
                    currentClassTimeRemaining = 3600,
                    isClassRunning = true
                ),
                isDarkMode = false,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Dark Mode - Current Class Running",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewDarkCurrentClass() {
    CollegeTimeTableTheme(darkTheme = true) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = sampleProfiles,
                selectedPersonId = samplePerson.id,
                timetable = sampleTimetable,
                classState = ClassState(
                    currentClass = sampleCurrentClass,
                    upcomingClass = sampleUpcomingClass,
                    remainingClasses = sampleRemainingClasses,
                    currentClassTimeRemaining = 1845,
                    isClassRunning = true
                ),
                isDarkMode = true,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Light Mode - Upcoming Class",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewLightUpcomingClass() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = sampleProfiles,
                selectedPersonId = sampleOtherPerson.id,
                timetable = sampleTimetable,
                classState = ClassState(
                    currentClass = null,
                    upcomingClass = sampleUpcomingClass,
                    remainingClasses = sampleRemainingClasses,
                    upcomingClassStartsIn = 900,
                    isClassRunning = false
                ),
                isDarkMode = false,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Light Mode - Empty Timetable",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewEmpty() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = sampleProfiles,
                selectedPersonId = samplePerson.id,
                timetable = TimetableData(),
                isDarkMode = false,
                isLoading = false
            ),
            onPersonSelected = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Loading State",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewLoading() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                profiles = sampleProfiles,
                selectedPersonId = samplePerson.id,
                timetable = TimetableData(),
                isDarkMode = false,
                isLoading = true
            ),
            onPersonSelected = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Full Timetable - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun FullTimetableScreenPreviewLight() {
    CollegeTimeTableTheme(darkTheme = false) {
        FullTimetableScreen(
            personName = samplePerson.name,
            timetable = sampleTimetable,
            isDarkMode = false,
            onBackClick = {},
            onAddClasses = {},
            onUpdateClassSchedule = { _, _, _ -> },
            onDeleteClass = { _, _ -> }
        )
    }
}

@Preview(
    name = "Full Timetable - Dark Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun FullTimetableScreenPreviewDark() {
    CollegeTimeTableTheme(darkTheme = true) {
        FullTimetableScreen(
            personName = samplePerson.name,
            timetable = sampleTimetable,
            isDarkMode = true,
            onBackClick = {},
            onAddClasses = {},
            onUpdateClassSchedule = { _, _, _ -> },
            onDeleteClass = { _, _ -> }
        )
    }
}

@Preview(
    name = "Full Timetable - Empty",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun FullTimetableScreenPreviewEmpty() {
    CollegeTimeTableTheme(darkTheme = false) {
        FullTimetableScreen(
            personName = samplePerson.name,
            timetable = TimetableData(),
            isDarkMode = false,
            onBackClick = {},
            onAddClasses = {},
            onUpdateClassSchedule = { _, _, _ -> },
            onDeleteClass = { _, _ -> }
        )
    }
}

@Preview(
    name = "People Management - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun PeopleManagementScreenPreviewLight() {
    CollegeTimeTableTheme(darkTheme = false) {
        PeopleManagementScreen(
            profiles = sampleProfiles,
            selectedPersonId = samplePerson.id,
            onBackClick = {},
            onSelectPerson = {},
            onAddPerson = {},
            onRenamePerson = { _, _ -> },
            onDeletePerson = {}
        )
    }
}

@Preview(
    name = "Day Schedule Card - Light Mode",
    showBackground = true
)
@Composable
private fun DayScheduleCardPreviewLight() {
    CollegeTimeTableTheme(darkTheme = false) {
        DayScheduleCard(
            day = "MONDAY",
            classes = sampleMondayClasses,
            isDarkMode = false
        )
    }
}

@Preview(
    name = "Day Schedule Card - Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF1A1C1E
)
@Composable
private fun DayScheduleCardPreviewDark() {
    CollegeTimeTableTheme(darkTheme = true) {
        DayScheduleCard(
            day = "TUESDAY",
            classes = sampleTuesdayClasses,
            isDarkMode = true
        )
    }
}

@Preview(
    name = "Class Item Row - Light Mode",
    showBackground = true
)
@Composable
private fun ClassItemRowPreviewLight() {
    CollegeTimeTableTheme(darkTheme = false) {
        ClassItemRow(
            classInfo = sampleMondayClasses[0],
            isDarkMode = false,
            accentColor = AppGreen
        )
    }
}

@Preview(
    name = "Class Item Row - Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF1A1C1E
)
@Composable
private fun ClassItemRowPreviewDark() {
    CollegeTimeTableTheme(darkTheme = true) {
        ClassItemRow(
            classInfo = sampleMondayClasses[1],
            isDarkMode = true,
            accentColor = AppGreenBright
        )
    }
}

@Preview(
    name = "View Full Timetable Button - Light Mode",
    showBackground = true
)
@Composable
private fun ViewFullTimetableButtonPreviewLight() {
    CollegeTimeTableTheme(darkTheme = false) {
        ViewFullTimetableButton(
            onClick = {},
            isDarkMode = false
        )
    }
}

@Preview(
    name = "View Full Timetable Button - Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF1A1C1E
)
@Composable
private fun ViewFullTimetableButtonPreviewDark() {
    CollegeTimeTableTheme(darkTheme = true) {
        ViewFullTimetableButton(
            onClick = {},
            isDarkMode = true
        )
    }
}
