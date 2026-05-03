package me.jitish.collegetimetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.ui.theme.AppBlueSoft
import me.jitish.collegetimetable.ui.theme.AppBlueSoftDark
import me.jitish.collegetimetable.ui.theme.AppTextStrongLight
import me.jitish.collegetimetable.ui.theme.AppSurfaceLight
import me.jitish.collegetimetable.ui.theme.AppBlueBright
import me.jitish.collegetimetable.ui.theme.AppBlue
import me.jitish.collegetimetable.ui.theme.AppOutlineDark
import me.jitish.collegetimetable.ui.theme.AppSurfaceDark
import me.jitish.collegetimetable.ui.theme.AppTextMutedDark
import me.jitish.collegetimetable.ui.theme.AppTextStrongDark
import me.jitish.collegetimetable.ui.theme.AppOutlineLight
import me.jitish.collegetimetable.ui.theme.AppGreenBright
import me.jitish.collegetimetable.ui.theme.AppGreen
import me.jitish.collegetimetable.ui.theme.AppGreenSoft
import me.jitish.collegetimetable.ui.theme.AppGreenSoftDark
import me.jitish.collegetimetable.ui.theme.AppTextMutedLight
import me.jitish.collegetimetable.ui.theme.AppSurfaceVariantLight
import me.jitish.collegetimetable.utils.formatTime
import me.jitish.collegetimetable.viewmodel.ClassState
import java.util.Locale

@Composable
fun ClassStatusDisplay(
    classState: ClassState,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            classState.currentClass != null -> {
                CurrentClassCard(
                    classInfo = classState.currentClass,
                    timeRemaining = classState.currentClassTimeRemaining,
                    isDarkMode = isDarkMode,
                    is24HourFormat = is24HourFormat
                )
                classState.upcomingClass?.let { upcoming ->
                    UpcomingClassCard(
                        classInfo = upcoming,
                        startsIn = classState.upcomingClassStartsIn,
                        isDarkMode = isDarkMode,
                        is24HourFormat = is24HourFormat
                    )
                }
            }

            classState.upcomingClass != null -> {
                UpcomingClassCard(
                    classInfo = classState.upcomingClass,
                    startsIn = classState.upcomingClassStartsIn,
                    isDarkMode = isDarkMode,
                    is24HourFormat = is24HourFormat
                )
            }

            else -> {
                NoClassCard(
                    message = "No more classes scheduled for today",
                    isDarkMode = isDarkMode
                )
            }
        }

        if (classState.remainingClasses.isNotEmpty()) {
            RemainingClassesSection(
                remainingClasses = classState.remainingClasses,
                isDarkMode = isDarkMode,
                is24HourFormat = is24HourFormat
            )
        }
    }
}

@Composable
private fun CurrentClassCard(
    classInfo: ClassInfo,
    timeRemaining: Long,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    ClassHighlightCard(
        label = "Current Class",
        classInfo = classInfo,
        timerLabel = "Ends in",
        timerSeconds = timeRemaining,
        isDarkMode = isDarkMode,
        cardColor = if (isDarkMode) AppGreenSoftDark else AppGreenSoft,
        accentColor = if (isDarkMode) AppGreenBright else AppGreen,
        modifier = modifier,
        is24HourFormat = is24HourFormat
    )
}

@Composable
private fun UpcomingClassCard(
    classInfo: ClassInfo,
    startsIn: Long,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    ClassHighlightCard(
        label = "Upcoming",
        classInfo = classInfo,
        timerLabel = "Starts in",
        timerSeconds = startsIn,
        isDarkMode = isDarkMode,
        cardColor = if (isDarkMode) AppBlueSoftDark else AppBlueSoft,
        accentColor = if (isDarkMode) AppBlueBright else AppBlue,
        modifier = modifier,
        is24HourFormat = is24HourFormat
    )
}

@Composable
private fun ClassHighlightCard(
    label: String,
    classInfo: ClassInfo,
    timerLabel: String,
    timerSeconds: Long,
    isDarkMode: Boolean,
    cardColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val contentColor = if (isDarkMode) AppTextStrongDark else AppTextStrongLight
    val courseCode = classInfo.courseCode
    val facultyName = classInfo.facultyName
    val subtleOverlay = if (isDarkMode) {
        Color.White.copy(alpha = 0.08f)
    } else {
        accentColor.copy(alpha = 0.15f)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = accentColor,
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = contentColor.copy(alpha = 0.9f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = classInfo.slot,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = accentColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = classInfo.courseTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (courseCode.isNotBlank()) {
                Text(
                    text = courseCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.75f)
                )
            }

            if (facultyName.isNotBlank()) {
                Text(
                    text = facultyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.68f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ClassMetaRow(
                classInfo = classInfo,
                contentColor = contentColor,
                is24HourFormat = is24HourFormat
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (timerSeconds >= 0) {
                TimerDisplay(
                    seconds = timerSeconds,
                    label = timerLabel,
                    backgroundColor = subtleOverlay,
                    textColor = contentColor
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = subtleOverlay,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Next scheduled day",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = contentColor,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassMetaRow(
    classInfo: ClassInfo,
    contentColor: Color,
    is24HourFormat: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = "Time",
                tint = contentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${formatTime(classInfo.start, is24HourFormat)} - ${formatTime(classInfo.end, is24HourFormat)}",
                color = contentColor.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = "Venue",
                tint = contentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = classInfo.venue,
                color = contentColor.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TimerDisplay(
    seconds: Long,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    val timeString = if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, secs)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = textColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeString,
                color = textColor,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun NoClassCard(
    message: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isDarkMode) AppSurfaceDark else AppSurfaceLight
    val contentColor = if (isDarkMode) AppTextMutedDark else AppTextMutedLight

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "All clear",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) AppGreenBright else AppGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = contentColor
            )
        }
    }
}

@Composable
private fun RemainingClassesSection(
    remainingClasses: List<ClassInfo>,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val contentColor = if (isDarkMode) AppTextStrongDark else AppTextStrongLight

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Later Today",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = contentColor.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        remainingClasses.forEach { classInfo ->
            RemainingClassItem(
                classInfo = classInfo,
                isDarkMode = isDarkMode,
                is24HourFormat = is24HourFormat
            )
        }
    }
}

@Composable
private fun RemainingClassItem(
    classInfo: ClassInfo,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true
) {
    val cardColor = if (isDarkMode) AppSurfaceDark else AppSurfaceVariantLight.copy(alpha = 0.5f)
    val contentColor = if (isDarkMode) AppTextStrongDark else AppTextStrongLight
    val mutedColor = if (isDarkMode) AppTextMutedDark else AppTextMutedLight

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = cardColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = classInfo.courseTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${formatTime(classInfo.start, is24HourFormat)} - ${formatTime(classInfo.end, is24HourFormat)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedColor
                    )
                    Text(
                        text = classInfo.venue,
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedColor
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isDarkMode) AppOutlineDark else AppOutlineLight
            ) {
                Text(
                    text = classInfo.slot,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = mutedColor
                )
            }
        }
    }
}
