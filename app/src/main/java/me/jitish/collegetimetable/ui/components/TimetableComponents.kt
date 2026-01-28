package me.jitish.collegetimetable.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.ui.theme.*
import me.jitish.collegetimetable.viewmodel.ClassState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDropdown(
    people: List<Person>,
    selectedPerson: Person?,
    onPersonSelected: (Person) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedPerson?.name ?: "Select a person",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Person") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            people.forEach { person ->
                DropdownMenuItem(
                    text = {
                        Text(
                            person.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onPersonSelected(person)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // icon colors
    val iconColor by animateColorAsState(
        targetValue = if (isDarkMode) MatteTaupeLight else MatteSlate,
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
            contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun CurrentClassCard(
    classInfo: ClassInfo,
    timeRemaining: Long,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isDarkMode) 
        Color(0xFF2A3A2A)
    else 
        Color(0xFFE8F0E6)
    val contentColor = if (isDarkMode) MatteNightText else MatteCharcoal
    val accentColor = if (isDarkMode) MatteSageDark else MatteSageLight
    val subtleOverlay = if (isDarkMode) Color.White.copy(alpha = 0.08f) else MatteSageLight.copy(alpha = 0.15f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Subtle shadow only
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
                        text = "Current Class",
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
                        color = if (isDarkMode) accentColor else MatteSageLight,
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

            Text(
                text = classInfo.courseCode,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        text = "${classInfo.start} - ${classInfo.end}",
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

            Spacer(modifier = Modifier.height(20.dp))

            // Timer showing when class will end
            TimerDisplay(
                seconds = timeRemaining,
                label = "Ends in",
                backgroundColor = subtleOverlay,
                textColor = contentColor
            )
        }
    }
}

@Composable
fun UpcomingClassCard(
    classInfo: ClassInfo,
    startsIn: Long,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isDarkMode) 
        Color(0xFF2A3340)
    else 
        Color(0xFFE8EEF4)
    val contentColor = if (isDarkMode) MatteNightText else MatteCharcoal
    val accentColor = if (isDarkMode) MatteDustyBlueDark else MatteDustyBlueLight
    val subtleOverlay = if (isDarkMode) Color.White.copy(alpha = 0.08f) else MatteDustyBlueLight.copy(alpha = 0.15f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Subtle shadow only
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
                        text = "Upcoming",
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
                        color = if (isDarkMode) accentColor else MatteDustyBlueLight,
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

            Text(
                text = classInfo.courseCode,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        text = "${classInfo.start} - ${classInfo.end}",
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

            Spacer(modifier = Modifier.height(20.dp))

            // Timer showing when class will start
            if (startsIn >= 0) {
                TimerDisplay(
                    seconds = startsIn,
                    label = "Starts in",
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
                        text = "Tomorrow",
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
fun TimerDisplay(
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
fun NoClassCard(
    message: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isDarkMode) MatteNightCard else MatteCream
    val contentColor = if (isDarkMode) MatteNightMuted else MatteSlate

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Minimal shadow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp), // Extra whitespace for calm feel
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✓",
                fontSize = 40.sp,
                color = if (isDarkMode) MatteSageDark else MatteSageLight
            )
            Spacer(modifier = Modifier.height(20.dp))
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
fun RemainingClassItem(
    classInfo: ClassInfo,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isDarkMode) MatteNightCard else MatteStone.copy(alpha = 0.5f)
    val contentColor = if (isDarkMode) MatteNightText else MatteCharcoal
    val mutedColor = if (isDarkMode) MatteNightMuted else MatteSlate

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
                        text = "${classInfo.start} - ${classInfo.end}",
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
                color = if (isDarkMode) MatteNightBorder else MattePebble
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

@Composable
fun RemainingClassesSection(
    remainingClasses: List<ClassInfo>,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isDarkMode) MatteNightText else MatteCharcoal

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
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
fun ClassStatusDisplay(
    classState: ClassState,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Show only one card: Current class takes priority, otherwise show upcoming
        when {
            // If a class is currently running, show only the current class card
            classState.currentClass != null -> {
                CurrentClassCard(
                    classInfo = classState.currentClass,
                    timeRemaining = classState.currentClassTimeRemaining,
                    isDarkMode = isDarkMode
                )
                // Show upcoming class if exists (when current class is running)
                classState.upcomingClass?.let { upcoming ->
                    UpcomingClassCard(
                        classInfo = upcoming,
                        startsIn = classState.upcomingClassStartsIn,
                        isDarkMode = isDarkMode
                    )
                }
            }
            // If no current class but there's an upcoming class, show only upcoming
            classState.upcomingClass != null -> {
                UpcomingClassCard(
                    classInfo = classState.upcomingClass,
                    startsIn = classState.upcomingClassStartsIn,
                    isDarkMode = isDarkMode
                )
            }
            // No classes at all
            else -> {
                NoClassCard(
                    message = "No more classes scheduled for today",
                    isDarkMode = isDarkMode
                )
            }
        }
        
        // Show remaining classes for the day
        if (classState.remainingClasses.isNotEmpty()) {
            RemainingClassesSection(
                remainingClasses = classState.remainingClasses,
                isDarkMode = isDarkMode
            )
        }
    }
}

