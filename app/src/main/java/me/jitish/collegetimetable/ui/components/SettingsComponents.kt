package me.jitish.collegetimetable.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.ui.theme.AppSurfaceLight
import me.jitish.collegetimetable.ui.theme.AppSurfaceDark
import me.jitish.collegetimetable.ui.theme.AppGreenBright
import me.jitish.collegetimetable.ui.theme.AppGreen

@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(250),
        label = "iconColor"
    )

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Icon(
            imageVector = if (isDarkMode) Icons.Outlined.WbSunny else Icons.Filled.Nightlight,
            contentDescription = if (isDarkMode) "Switch to light mode" else "Switch to dark mode",
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(250),
        label = "settingsIconColor"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Settings",
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun SettingsDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    is24HourFormat: Boolean,
    onToggle24HourFormat: () -> Unit,
    isDarkMode: Boolean
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Settings",
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Time Format",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (is24HourFormat) "24-hour (14:30)" else "12-hour (2:30 PM)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = is24HourFormat,
                        onCheckedChange = { onToggle24HourFormat() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = if (isDarkMode) AppGreenBright else AppGreen,
                            checkedTrackColor = if (isDarkMode) {
                                AppGreenBright.copy(alpha = 0.3f)
                            } else {
                                AppGreen.copy(alpha = 0.3f)
                            }
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        containerColor = if (isDarkMode) AppSurfaceDark else AppSurfaceLight,
        shape = RoundedCornerShape(16.dp)
    )
}
