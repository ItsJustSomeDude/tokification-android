package net.itsjustsomedude.tokens.ui

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.SimpleDialogs
import net.itsjustsomedude.tokens.models.EventViewModel
import net.itsjustsomedude.tokens.models.ModelFactory
import java.util.Calendar

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

    val context = LocalContext.current as? ComponentActivity
    val dateFormatter = remember {
        DateFormat.getDateFormat(context)
    }

    val timeFormatter = remember {
        DateFormat.getTimeFormat(context)
    }

    val coop by model.coop.observeAsState()
    val modelEvent by model.event.observeAsState()
    modelEvent?.let { event ->
        var playerMenuExpanded by remember { mutableStateOf(false) }
        var selectedPlayer by remember { mutableStateOf(coop?.players?.get(0)) }
        var customPlayerVisible by remember { mutableStateOf(false) }
        var customPlayer by remember { mutableStateOf("") }
        var count by remember { mutableIntStateOf(6) }
        val countPattern = remember { Regex("^\\d{1,4}\$") }

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
                        coop?.players?.forEach { selectionOption ->
                            println("Added ddl item $selectionOption")
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
//                    modifier = Modifier.menuAnchor(),
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

            Row {
                IconButton(onClick = {
                    if (count != 0) {
                        count--
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Subtract 1"
                    )
                }

                TextField(
                    value = count.toString(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        if (it.matches(countPattern)) {
                            count = it.toInt()
                        } else if (it.isEmpty()) {
                            count = 0
                        }
                    },
                    modifier = Modifier.width(64.dp)
                )

                IconButton(onClick = {
                    if (count != 9999) {
                        count++
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Add 1"
                    )
                }
            }

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
    } ?: Text(text = "No event!")
}

@Composable
fun DateTimeRow(
    modifier: Modifier = Modifier,
    date: Calendar?,
    dateLabel: String,
    timeLabel: String,
    unsetText: String,
    onChange: (date: Calendar?) -> Unit
) {
    val context = LocalContext.current as? Context

    val dateFormatter = remember {
        DateFormat.getDateFormat(context)
    }

    val timeFormatter = remember {
        DateFormat.getTimeFormat(context)
    }

    Row {
        TimeButton(
            label = dateLabel,
            btnText = if (date == null) unsetText else dateFormatter.format(date.time),
            onClick = {
                SimpleDialogs.datePicker(context, date) {
                    onChange(it)
                }
            }
        )

        TimeButton(
            label = timeLabel,
            btnText = if (date == null) unsetText else timeFormatter.format(date.time),
            onClick = {
                SimpleDialogs.timePicker(context, date) {
                    onChange(it)
                }
            }
        )
    }
}