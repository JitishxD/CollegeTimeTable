package me.jitish.collegetimetable.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
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
import me.jitish.collegetimetable.ui.theme.AppTextMutedLight
import me.jitish.collegetimetable.ui.theme.AppAmber
import me.jitish.collegetimetable.ui.theme.AppAmberBright
import me.jitish.collegetimetable.ui.theme.AppCoral
import me.jitish.collegetimetable.ui.theme.AppCoralBright
import me.jitish.collegetimetable.ui.theme.AppCyan
import me.jitish.collegetimetable.ui.theme.AppCyanBright
import me.jitish.collegetimetable.ui.theme.AppPurple
import me.jitish.collegetimetable.ui.theme.AppPurpleBright
import me.jitish.collegetimetable.ui.theme.AppRed
import me.jitish.collegetimetable.ui.theme.AppRedBright
import me.jitish.collegetimetable.utils.formatTime
import java.util.Locale

@Composable
fun DayScheduleCard(
    day: String,
    classes: List<ClassInfo>,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = true,
    onEditClass: ((ClassInfo) -> Unit)? = null,
    onDeleteClass: ((ClassInfo) -> Unit)? = null
) {
    val dayColor = getDayColor(day, isDarkMode)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppSurfaceDark else AppSurfaceLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = dayColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = day.toDisplayDay(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = dayColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${classes.size} class${if (classes.size == 1) "" else "es"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (classes.isEmpty()) {
                Text(
                    text = "No classes scheduled",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                classes.sortedBy { it.start }.forEachIndexed { index, classInfo ->
                    ClassItemRow(
                        classInfo = classInfo,
                        isDarkMode = isDarkMode,
                        is24HourFormat = is24HourFormat,
                        accentColor = dayColor,
                        onEditClass = onEditClass,
                        onDeleteClass = onDeleteClass
                    )

                    if (index < classes.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = if (isDarkMode) AppOutlineDark else AppOutlineLight,
                            thickness = 0.5.dp
                        )
                    }
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
    is24HourFormat: Boolean = true,
    onEditClass: ((ClassInfo) -> Unit)? = null,
    onDeleteClass: ((ClassInfo) -> Unit)? = null
) {
    val contentColor = if (isDarkMode) AppTextStrongDark else AppTextStrongLight
    val courseCode = classInfo.courseCode
    val facultyName = classInfo.facultyName

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
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

        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(3.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor.copy(alpha = 0.5f))
        )

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
                if (courseCode.isNotBlank()) {
                    Text(
                        text = courseCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

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
                        color = contentColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (facultyName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = facultyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.62f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
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

            if (onEditClass != null || onDeleteClass != null) {
                Row {
                    if (onEditClass != null) {
                        IconButton(
                            onClick = { onEditClass(classInfo) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit ${classInfo.courseTitle}",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    if (onDeleteClass != null) {
                        IconButton(
                            onClick = { onDeleteClass(classInfo) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete ${classInfo.courseTitle}",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getDayColor(day: String, isDarkMode: Boolean): Color {
    return when (day.uppercase()) {
        "MONDAY" -> if (isDarkMode) AppGreenBright else AppGreen
        "TUESDAY" -> if (isDarkMode) AppBlueBright else AppBlue
        "WEDNESDAY" -> if (isDarkMode) AppAmberBright else AppAmber
        "THURSDAY" -> if (isDarkMode) AppPurpleBright else AppPurple
        "FRIDAY" -> if (isDarkMode) AppCoralBright else AppCoral
        "SATURDAY" -> if (isDarkMode) AppCyanBright else AppCyan
        "SUNDAY" -> if (isDarkMode) AppRedBright else AppRed
        else -> if (isDarkMode) AppTextMutedDark else AppTextMutedLight
    }
}

fun String.toDisplayDay(): String {
    return lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}
