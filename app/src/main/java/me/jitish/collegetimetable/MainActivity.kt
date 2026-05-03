package me.jitish.collegetimetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.jitish.collegetimetable.ui.screens.FullTimetableScreen
import me.jitish.collegetimetable.ui.screens.PeopleManagementScreen
import me.jitish.collegetimetable.ui.screens.TimetableScreen
import me.jitish.collegetimetable.ui.theme.CollegeTimeTableTheme
import me.jitish.collegetimetable.viewmodel.TimetableViewModel

private enum class AppDestination {
    Dashboard,
    FullTimetable,
    People
}

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
                var destination by remember { mutableStateOf(AppDestination.Dashboard) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BackHandler(enabled = destination != AppDestination.Dashboard) {
                        destination = AppDestination.Dashboard
                    }

                    when (destination) {
                        AppDestination.Dashboard -> {
                            TimetableScreen(
                                uiState = uiState,
                                onPersonSelected = viewModel::selectPerson,
                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                onToggle24HourFormat = { viewModel.toggle24HourFormat() },
                                onManagePeople = { destination = AppDestination.People },
                                onViewFullTimetable = { destination = AppDestination.FullTimetable }
                            )
                        }

                        AppDestination.FullTimetable -> {
                            FullTimetableScreen(
                                personName = uiState.selectedPerson?.name.orEmpty(),
                                timetable = uiState.timetable,
                                isDarkMode = uiState.isDarkMode,
                                is24HourFormat = uiState.is24HourFormat,
                                onBackClick = { destination = AppDestination.Dashboard },
                                onAddClasses = viewModel::addClasses,
                                onUpdateClassSchedule = viewModel::updateClassSchedule,
                                onDeleteClass = viewModel::deleteClass
                            )
                        }

                        AppDestination.People -> {
                            PeopleManagementScreen(
                                profiles = uiState.profiles,
                                selectedPersonId = uiState.selectedPersonId,
                                onBackClick = { destination = AppDestination.Dashboard },
                                onSelectPerson = viewModel::selectPerson,
                                onAddPerson = viewModel::addPerson,
                                onRenamePerson = viewModel::renamePerson,
                                onDeletePerson = viewModel::deletePerson
                            )
                        }
                    }
                }
            }
        }
    }
}
