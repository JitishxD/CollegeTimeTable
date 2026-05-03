package me.jitish.collegetimetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.jitish.collegetimetable.data.Person
import me.jitish.collegetimetable.ui.components.*
import me.jitish.collegetimetable.ui.theme.CollegeTimeTableTheme
import me.jitish.collegetimetable.viewmodel.TimetableUiState
import me.jitish.collegetimetable.viewmodel.TimetableViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TimetableViewModel = viewModel(
                factory = TimetableViewModel.Factory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsState()

            CollegeTimeTableTheme(darkTheme = uiState.isDarkMode) {
                var showFullTimetable by remember { mutableStateOf(false) }
                val selectedPerson = uiState.selectedPerson

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Handle system back button
                    BackHandler(enabled = showFullTimetable) {
                        showFullTimetable = false
                    }

                    if (showFullTimetable && selectedPerson != null) {
                        FullTimetableScreen(
                            personName = selectedPerson.name,
                            timetable = uiState.timetable,
                            isDarkMode = uiState.isDarkMode,
                            is24HourFormat = uiState.is24HourFormat,
                            onBackClick = { showFullTimetable = false }
                        )
                    } else {
                        TimetableScreen(
                            uiState = uiState,
                            onPersonSelected = { viewModel.selectPerson(it) },
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onToggle24HourFormat = { viewModel.toggle24HourFormat() },
                            onViewFullTimetable = { showFullTimetable = true }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    uiState: TimetableUiState,
    onPersonSelected: (Person) -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier,
    onToggle24HourFormat: () -> Unit = {},
    onViewFullTimetable: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Settings Dialog
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
                        onClick = { showSettingsDialog = true },
                        isDarkMode = uiState.isDarkMode
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
                .padding(horizontal = 20.dp) // Increased horizontal padding
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp) // Increased spacing
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Person Dropdown
            if (uiState.people.isNotEmpty()) {
                PersonDropdown(
                    people = uiState.people,
                    selectedPerson = uiState.selectedPerson,
                    onPersonSelected = onPersonSelected
                )
            }

            // Loading State
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
            } else if (uiState.selectedPerson != null) {
                // Class Status Display
                ClassStatusDisplay(
                    classState = uiState.classState,
                    isDarkMode = uiState.isDarkMode,
                    is24HourFormat = uiState.is24HourFormat
                )

                // View Full Timetable Button
                ViewFullTimetableButton(
                    onClick = onViewFullTimetable,
                    isDarkMode = uiState.isDarkMode
                )
            } else {
                // No person selected - matte styled card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "↑",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Select a person to view their timetable",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
