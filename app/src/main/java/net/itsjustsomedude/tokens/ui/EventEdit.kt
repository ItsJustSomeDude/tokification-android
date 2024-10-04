package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.R
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.models.EventEditViewModel

@Composable
fun EventEditDialog(
    modifier: Modifier = Modifier,
    dialogKey: String = "",
    coopId: Long,
    eventId: Long?,
    showExtendedButtons: Boolean = true,
    showBoostOptions: Boolean = false,
    onDismiss: () -> Unit,
    model: EventEditViewModel = EventEditViewModel.provide(dialogKey, coopId, eventId)
) {
    val coop by model.coop.observeAsState()
    val event by model.event.observeAsState()

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                model.save()
                onDismiss()
            }) {
                Text("Done")
            }
        },
        text = {
            event?.let { ev ->
                EventEdit(
                    event = ev,
                    players = coop?.players ?: emptyList(),
                    showExtendedButtons = showExtendedButtons,
                    showBoostOptions = showBoostOptions,
                    onChanged = {
                        model.updateEvent(it)
                    }
                )
            } ?: EventEditSkeleton(showExtendedButtons = showExtendedButtons)
        })
}


//@Composable
//fun EventEditDialog(
//    modifier: Modifier = Modifier,
//    event: Event?,
//    players: List<String>,
//    showExtendedButtons: Boolean = true,
//    showBoostOptions: Boolean = false,
//    onDismissRequest: () -> Unit,
//    onDoneClicked: () -> Unit,
//    onChanged: (Event) -> Unit
//) {
//    AlertDialog(
//        modifier = modifier,
//        onDismissRequest = onDismissRequest,
//        confirmButton = {
//            Button(onClick = onDoneClicked) {
//                Text("Done")
//            }
//        },
//        text = {
//            event?.let {
//                EventEdit(
//                    event = it,
//                    players = players,
//                    showExtendedButtons = showExtendedButtons,
//                    onChanged = onChanged
//                )
//            } ?: EventEditSkeleton(showExtendedButtons = showExtendedButtons)
//        })
//}

@Composable
fun EventEditSkeleton(
    modifier: Modifier = Modifier,
    showExtendedButtons: Boolean = true,
    showBoostOptions: Boolean = false,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            Modifier
                .size(218.dp, 20.dp)
                .clip(RoundedCornerShape(8.dp))
                .skeletonColors()
        )

        Box(
            Modifier
                .size(272.dp, 56.dp)
                .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                .skeletonColors()
        )

        if (showExtendedButtons)
            Box(
                Modifier
                    .size(212.dp, 48.dp)
                    .clip(RoundedCornerShape(50))
                    .skeletonColors()
            )

        NumberEntrySkeleton()

        if (showExtendedButtons)
            DateTimeRowSkeleton()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEdit(
    modifier: Modifier = Modifier,
    event: Event,
    players: List<String>,
    showExtendedButtons: Boolean = true,
    showBoostOptions: Boolean = false,
    onChanged: (Event) -> Unit,
    // TODO: Give this its own ViewModel.
) {
    var playerMenuExpanded by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf(event.person) }
    var customPlayerMode by remember {
        mutableStateOf(
            event.person.isNotBlank() &&
                    !players.contains(event.person)
        )
    }
    var customPlayer by remember { mutableStateOf(if (customPlayerMode) event.person else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text("Coop: ${event.coop}, Contract: ${event.kevId}")

        ExposedDropdownMenuBox(
            expanded = playerMenuExpanded,
            onExpandedChange = {
                playerMenuExpanded = !playerMenuExpanded
            },
            modifier = Modifier.debugRuler(LocalDensity.current, "DDL")
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = !customPlayerMode,
                singleLine = true,
                value = if (customPlayerMode) customPlayer else selectedPlayer,
                onValueChange = {
                    if (customPlayerMode) {
                        customPlayer = it

                        onChanged(
                            event.copy(
                                person = it
                            )
                        )
                    }
                },
                label = {
                    Text(
                        if (customPlayerMode) "Enter Player" else "Select Player"
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerMenuExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = playerMenuExpanded,
                onDismissRequest = { playerMenuExpanded = false },
            ) {
                players.forEach { player ->
                    DropdownMenuItem(
                        text = { Text(text = player) },
                        onClick = {
                            customPlayerMode = false
                            selectedPlayer = player
                            playerMenuExpanded = false

                            onChanged(
                                event.copy(
                                    person = player
                                )
                            )
                        },
                    )
                }

                DropdownMenuItem(
                    text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.offline_bolt),
                                contentDescription = ""
                            )
                            Text(text = "Sink", fontStyle = FontStyle.Italic)
                        }
                    },
                    onClick = {
                        customPlayerMode = false
                        selectedPlayer = "Sink"
                        playerMenuExpanded = false
                    },
                )

                DropdownMenuItem(
                    text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = ""
                            )
                            Text(text = "Add a player", fontStyle = FontStyle.Italic)
                        }
                    },
                    onClick = {
                        selectedPlayer = "Add a player"
                        playerMenuExpanded = false
                        customPlayerMode = true
                    },
                )
            }
        }

        if (showExtendedButtons)
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.debugRuler(LocalDensity.current, "ButtonRow")
            ) {
                SegmentedButton(
                    selected = event.direction == Event.DIRECTION_SENT,
                    onClick = {
                        onChanged(
                            event.copy(
                                direction = Event.DIRECTION_SENT
                            )
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) {
                    Text("Sent")
                }

                SegmentedButton(
                    selected = event.direction == Event.DIRECTION_RECEIVED,
                    onClick = {
                        onChanged(
                            event.copy(
                                direction = Event.DIRECTION_RECEIVED
                            )
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) {
                    Text("Received")
                }
            }

        NumberEntry(
            modifier = Modifier.debugRuler(LocalDensity.current, "NumberEntry"),
            value = event.count, onChanged = {
                onChanged(
                    event.copy(
                        count = it
                    )
                )
            })

        if (showExtendedButtons)
            DateTimeRow(
                modifier = Modifier.debugRuler(LocalDensity.current, "DateRow"),
                dateLabel = "Date",
                timeLabel = "Time",
                unsetText = "Set",
                date = event.time,
                onChange = {
                    it?.let {
                        onChanged(
                            event.copy(
                                time = it
                            )
                        )
                    }
                }
            )
    }
}

//@Preview
//@Composable
//fun PreviewNullEventEditDialog() {
//    EventEditDialog(
//        event = null,
//        players = emptyList(),
//        onDoneClicked = {},
//        onChanged = {},
//        onDismissRequest = {}
//    )
//}
//
//@Preview
//@Composable
//fun PreviewEventEditDialog() {
//    EventEditDialog(
//        event = Event(
//            coop = "coop",
//            kevId = "kev-id",
//            time = Calendar.getInstance(),
//            count = 4,
//            person = "SomePlayer",
//            direction = Event.DIRECTION_RECEIVED
//        ),
//        players = listOf("Player1", "Player2", "SomePlayer"),
//        onDoneClicked = {},
//        onChanged = {},
//        onDismissRequest = {}
//    )
//}
//
//@Preview
//@Composable
//fun PreviewShortEventEditDialog() {
//    EventEditDialog(
//        event = Event(
//            coop = "coop",
//            kevId = "kev-id",
//            time = Calendar.getInstance(),
//            count = 4,
//            person = "SomePlayer",
//            direction = Event.DIRECTION_RECEIVED
//        ),
//        players = listOf("Player1", "Player2", "SomePlayer"),
//        showExtendedButtons = false,
//        onDoneClicked = {},
//        onChanged = {},
//        onDismissRequest = {}
//    )
//}
//
//@Preview
//@Composable
//fun PreviewShortNullEventEditDialog() {
//    EventEditDialog(
//        event = null,
//        players = emptyList(),
//        showExtendedButtons = false,
//        onDoneClicked = {},
//        onChanged = {},
//        onDismissRequest = {}
//    )
//}
