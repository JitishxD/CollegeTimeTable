package me.jitish.collegetimetable.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import me.jitish.collegetimetable.data.TimetableProfile
import me.jitish.collegetimetable.ui.components.ConfirmDeletePersonDialog
import me.jitish.collegetimetable.ui.components.PersonNameDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleManagementScreen(
    profiles: List<TimetableProfile>,
    selectedPersonId: String?,
    onBackClick: () -> Unit,
    onSelectPerson: (Person) -> Unit,
    onAddPerson: (String) -> Unit,
    onRenamePerson: (String, String) -> Unit,
    onDeletePerson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var personToRename by remember { mutableStateOf<Person?>(null) }
    var personToDelete by remember { mutableStateOf<Person?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Manage People",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Create and switch between timetables",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add person"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = profiles,
                key = { it.person.id }
            ) { profile ->
                PersonProfileCard(
                    profile = profile,
                    isSelected = profile.person.id == selectedPersonId,
                    canDelete = profiles.size > 1,
                    onSelect = { onSelectPerson(profile.person) },
                    onRename = { personToRename = profile.person },
                    onDelete = { personToDelete = profile.person }
                )
            }
        }
    }

    if (showAddDialog) {
        PersonNameDialog(
            title = "Add person",
            confirmLabel = "Add",
            initialName = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                onAddPerson(name)
                showAddDialog = false
            }
        )
    }

    personToRename?.let { person ->
        PersonNameDialog(
            title = "Rename person",
            confirmLabel = "Save",
            initialName = person.name,
            onDismiss = { personToRename = null },
            onConfirm = { name ->
                onRenamePerson(person.id, name)
                personToRename = null
            }
        )
    }

    personToDelete?.let { person ->
        ConfirmDeletePersonDialog(
            person = person,
            onDismiss = { personToDelete = null },
            onConfirm = {
                onDeletePerson(person.id)
                personToDelete = null
            }
        )
    }
}

@Composable
private fun PersonProfileCard(
    profile: TimetableProfile,
    isSelected: Boolean,
    canDelete: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val classCount = profile.timetable.timetable.values.sumOf { it.size }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.person.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "$classCount saved class${if (classCount == 1) "" else "es"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onRename) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Rename ${profile.person.name}"
                )
            }
            IconButton(
                onClick = onDelete,
                enabled = canDelete
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete ${profile.person.name}",
                    tint = if (canDelete) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    }
                )
            }
        }
    }
}
