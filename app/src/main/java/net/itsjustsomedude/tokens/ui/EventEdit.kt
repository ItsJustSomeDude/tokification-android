package net.itsjustsomedude.tokens.ui

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.models.EventViewModel
import net.itsjustsomedude.tokens.models.ModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEdit(
    eventId: Long,
    modifier: Modifier = Modifier,
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModelFactory = ModelFactory(application, eventId)
    val model: EventViewModel = viewModel(factory = viewModelFactory)

//    LaunchedEffect(key1 = eventId) {
//        model.setEvent(eventId)
//    }

    val coop by model.coop.observeAsState()
    val modelEvent by model.event.observeAsState()
    modelEvent?.let { event ->

    } ?: Text(text = "No event!")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventInfo(
    event: Event,
    players: List<String>
) {
    var playerMenuExpanded by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf(players[0]) }
    var customPlayerVisible by remember { mutableStateOf(false) }
    var customPlayer by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(6) }

    Column {
        Text("Coop: ${event.coop}, Contract: ${event.kevId}")
        Row {
            ExposedDropdownMenuBox(expanded = playerMenuExpanded, onExpandedChange = {
                playerMenuExpanded = !playerMenuExpanded
            }) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedPlayer ?: "",
                    onValueChange = {},
                    label = { Text("Label") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = playerMenuExpanded,
                    onDismissRequest = { playerMenuExpanded = false },
                ) {
                    players.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                customPlayerVisible = false
                                selectedPlayer = selectionOption
                                playerMenuExpanded = false
                            },
                        )
                    }

                    DropdownMenuItem(
                        text = { Text(text = "Add a player", fontStyle = FontStyle.Italic) },
                        onClick = {
                            selectedPlayer = "Add a player"
                            playerMenuExpanded = false
                            customPlayerVisible = true
                        },
                    )
                }
            }
        }

        if (customPlayerVisible) {
            TextField(
                value = customPlayer,
                onValueChange = {
                    customPlayer = it
                },
                label = { Text("Player") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Player"
                    )
                }
            )
        }

        NumberEntry(value = count, onChange = { count = it })

        DateTimeRow(
            dateLabel = "Date",
            timeLabel = "Time",
            unsetText = "Set",
            date = event.time,
            onChange = {
                event.time = it
            }
        )

        Button(onClick = {
            model.update(event)
        }) {
            Text("Save")
        }
    }
}
