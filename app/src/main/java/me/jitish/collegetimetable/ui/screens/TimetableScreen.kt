package me.jitish.collegetimetable.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.ui.components.ClassStatusDisplay
import me.jitish.collegetimetable.ui.components.EmptyTimetableCard
import me.jitish.collegetimetable.ui.components.ManagePeopleButton
import me.jitish.collegetimetable.ui.components.PersonDropdown
import me.jitish.collegetimetable.ui.components.SettingsButton
import me.jitish.collegetimetable.ui.components.SettingsDialog
import me.jitish.collegetimetable.ui.components.ThemeToggleButton
import me.jitish.collegetimetable.ui.components.ViewFullTimetableButton
import me.jitish.collegetimetable.viewmodel.TimetableUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    uiState: TimetableUiState,
    onPersonSelected: (Person) -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier,
    onToggle24HourFormat: () -> Unit = {},
    onManagePeople: () -> Unit = {},
    onViewFullTimetable: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    SettingsDialog(
        showDialog = showSettingsDialog,
        onDismiss = { showSettingsDialog = false },
        is24HourFormat = uiState.is24HourFormat,
        onToggle24HourFormat = onToggle24HourFormat,
        isDarkMode = uiState.isDarkMode
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Timetable",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    SettingsButton(
                        onClick = { showSettingsDialog = true }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ThemeToggleButton(
                        isDarkMode = uiState.isDarkMode,
                        onToggle = onToggleDarkMode
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PersonDropdown(
                        people = uiState.people,
                        selectedPerson = uiState.selectedPerson,
                        onPersonSelected = onPersonSelected,
                        modifier = Modifier.weight(1f)
                    )
                    ManagePeopleButton(onClick = onManagePeople)
                }

                if (uiState.hasTimetableEntries) {
                    ClassStatusDisplay(
                        classState = uiState.classState,
                        isDarkMode = uiState.isDarkMode,
                        is24HourFormat = uiState.is24HourFormat
                    )
                } else {
                    EmptyTimetableCard(
                        onManageClick = onViewFullTimetable,
                        isDarkMode = uiState.isDarkMode
                    )
                }

                ViewFullTimetableButton(
                    onClick = onViewFullTimetable,
                    isDarkMode = uiState.isDarkMode
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
