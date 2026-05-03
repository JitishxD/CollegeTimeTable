package me.jitish.collegetimetable.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.jitish.collegetimetable.TimetableScreen
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.ui.theme.CollegeTimeTableTheme
import me.jitish.collegetimetable.ui.theme.MatteSageDark
import me.jitish.collegetimetable.ui.theme.MatteSageLight
import me.jitish.collegetimetable.viewmodel.ClassState
import me.jitish.collegetimetable.viewmodel.TimetableUiState

// Sample data for previews
private val samplePeople = listOf(
    Person("Jitish", "Jitish.json"),
    Person("Utkarsh", "Utkarsh.json")
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
    ClassInfo(
        slot = "B11",
        courseCode = "ECE2002",
        courseTitle = "Digital Logic Design",
        start = "10:05",
        end = "11:35",
        venue = "AB-414"
    ),
    ClassInfo(
        slot = "C11",
        courseCode = "MAT3002",
        courseTitle = "Applied Linear Algebra",
        start = "11:40",
        end = "13:10",
        venue = "AB-126"
    )
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
                people = samplePeople,
                selectedPerson = samplePeople[0],
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
    name = "Dark Mode (Midnight) - Current Class Running",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewDarkCurrentClass() {
    CollegeTimeTableTheme(darkTheme = true) {
        TimetableScreen(
            uiState = TimetableUiState(
                people = samplePeople,
                selectedPerson = samplePeople[0],
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
                people = samplePeople,
                selectedPerson = samplePeople[1],
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
    name = "Dark Mode (Midnight) - Upcoming Class",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewDarkUpcomingClass() {
    CollegeTimeTableTheme(darkTheme = true) {
        TimetableScreen(
            uiState = TimetableUiState(
                people = samplePeople,
                selectedPerson = samplePeople[1],
                classState = ClassState(
                    currentClass = null,
                    upcomingClass = sampleUpcomingClass,
                    remainingClasses = sampleRemainingClasses,
                    upcomingClassStartsIn = 7200,
                    isClassRunning = false
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
    name = "Light Mode - No Classes",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewNoClasses() {
    CollegeTimeTableTheme(darkTheme = false) {
        TimetableScreen(
            uiState = TimetableUiState(
                people = samplePeople,
                selectedPerson = samplePeople[0],
                classState = ClassState(
                    currentClass = null,
                    upcomingClass = null,
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
    name = "Dark Mode (Midnight) - No Person Selected",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TimetableScreenPreviewNoPersonSelected() {
    CollegeTimeTableTheme(darkTheme = true) {
        TimetableScreen(
            uiState = TimetableUiState(
                people = samplePeople,
                selectedPerson = null,
                isDarkMode = true,
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
                people = emptyList(),
                selectedPerson = null,
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
            personName = "Jitish",
            timetable = sampleTimetable,
            isDarkMode = false,
            onBackClick = {}
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
            personName = "Jitish",
            timetable = sampleTimetable,
            isDarkMode = true,
            onBackClick = {}
        )
    }
}

@Preview(
    name = "Full Timetable - No Data",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun FullTimetableScreenPreviewNoData() {
    CollegeTimeTableTheme(darkTheme = false) {
        FullTimetableScreen(
            personName = "Jitish",
            timetable = null,
            isDarkMode = false,
            onBackClick = {}
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
            accentColor = MatteSageLight
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
            accentColor = MatteSageDark
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
