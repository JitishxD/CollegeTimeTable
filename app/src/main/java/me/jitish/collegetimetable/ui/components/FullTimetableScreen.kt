package me.jitish.collegetimetable.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullTimetableScreen(
    personName: String,
    timetable: TimetableData?,
    isDarkMode: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val verticalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "$personName's Timetable",
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
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (timetable == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No timetable data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(verticalScrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val daysOrder = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

                daysOrder.forEach { day ->
                    val classes = timetable.timetable[day]
                    if (!classes.isNullOrEmpty()) {
                        DayScheduleCard(
                            day = day,
                            classes = classes.sortedBy { it.start },
                            isDarkMode = isDarkMode,
                            is24HourFormat = is24HourFormat
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DayScheduleCard(
    day: String,
    classes: List<ClassInfo>,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val dayColors = getDayColor(day, isDarkMode)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) MatteNightCard else MatteCream
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Day Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = dayColors.first.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = day.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = dayColors.first,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${classes.size} class${if (classes.size > 1) "es" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Classes List
            classes.forEachIndexed { index, classInfo ->
                ClassItemRow(
                    classInfo = classInfo,
                    isDarkMode = isDarkMode,
                    is24HourFormat = is24HourFormat,
                    accentColor = dayColors.first
                )

                if (index < classes.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = if (isDarkMode) MatteNightBorder else MattePebble,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ClassItemRow(
    classInfo: ClassInfo,
    isDarkMode: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val contentColor = if (isDarkMode) MatteNightText else MatteCharcoal

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Time Column
        Column(
            modifier = Modifier.width(if (is24HourFormat) 70.dp else 90.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = formatTime(classInfo.start, is24HourFormat),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = formatTime(classInfo.end, is24HourFormat),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.6f)
            )
        }

        // Divider line
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(3.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor.copy(alpha = 0.5f))
        )

        // Class Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = classInfo.courseTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Course Code
                Text(
                    text = classInfo.courseCode,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )

                // Venue
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "Venue",
                        tint = contentColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = classInfo.venue,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Slot Badge
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = accentColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = classInfo.slot,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = accentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ViewFullTimetableButton(
    onClick: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonColor = if (isDarkMode) MatteDustyBlueDark else MatteDustyBlueLight

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = buttonColor
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(buttonColor.copy(alpha = 0.5f))
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "View Full Weekly Timetable",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper function to get day-specific colors
private fun getDayColor(day: String, isDarkMode: Boolean): Pair<Color, Color> {
    return when (day.uppercase()) {
        "MONDAY" -> if (isDarkMode) Pair(MatteSageDark, MatteSageDark.copy(alpha = 0.2f))
                    else Pair(MatteSageLight, MatteSageLight.copy(alpha = 0.2f))
        "TUESDAY" -> if (isDarkMode) Pair(MatteDustyBlueDark, MatteDustyBlueDark.copy(alpha = 0.2f))
                     else Pair(MatteDustyBlueLight, MatteDustyBlueLight.copy(alpha = 0.2f))
        "WEDNESDAY" -> if (isDarkMode) Pair(MatteTaupeLight, MatteTaupeLight.copy(alpha = 0.2f))
                       else Pair(MatteTaupe, MatteTaupe.copy(alpha = 0.2f))
        "THURSDAY" -> if (isDarkMode) Pair(Color(0xFFB39DDB), Color(0xFFB39DDB).copy(alpha = 0.2f))
                      else Pair(Color(0xFF9575CD), Color(0xFF9575CD).copy(alpha = 0.2f))
        "FRIDAY" -> if (isDarkMode) Pair(MatteTerracotta, MatteTerracotta.copy(alpha = 0.2f))
                    else Pair(Color(0xFFBF8A7A), Color(0xFFBF8A7A).copy(alpha = 0.2f))
        "SATURDAY" -> if (isDarkMode) Pair(Color(0xFF90A4AE), Color(0xFF90A4AE).copy(alpha = 0.2f))
                      else Pair(Color(0xFF78909C), Color(0xFF78909C).copy(alpha = 0.2f))
        "SUNDAY" -> if (isDarkMode) Pair(Color(0xFFE57373), Color(0xFFE57373).copy(alpha = 0.2f))
                    else Pair(Color(0xFFEF5350), Color(0xFFEF5350).copy(alpha = 0.2f))
        else -> if (isDarkMode) Pair(MatteNightMuted, MatteNightMuted.copy(alpha = 0.2f))
                else Pair(MatteSlate, MatteSlate.copy(alpha = 0.2f))
    }
}

// -------- PREVIEWS --------

// Sample data for previews
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
