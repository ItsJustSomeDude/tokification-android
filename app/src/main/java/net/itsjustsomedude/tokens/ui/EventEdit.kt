package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
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
        title = { Text("Token Transaction") },
        text = {
            event?.let { ev ->
                EventEdit(
                    event = ev,
                    players = (
                            if (coop?.sinkMode == true)
                                coop?.players
                            else
                                listOf("Sink") + (coop?.players ?: emptyList())
                            )
                        ?: emptyList(),
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

@OptIn(ExperimentalFoundationApi::class)
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
    var selectedPlayer by remember { mutableStateOf(event.person) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
//        Text("Coop: ${event.coop}, Contract: ${event.kevId}")

        // TODO: Meh...
        Text(
            "If sending to the Sink, leave this blank.",
            fontStyle = FontStyle.Italic
        )
        TextFieldMenu(
            label = "Select Player",
            options = players,
            selectedOption = selectedPlayer,
            onOptionSelected = {
                selectedPlayer = it ?: ""

                onChanged(
                    event.copy(
                        person = it ?: ""
                    )
                )
            }
        )

        if (showExtendedButtons) {
            Text(
                modifier = Modifier.padding(PaddingValues(top = 8.dp)),
                text = "Token Direction",
                style = MaterialTheme.typography.bodySmall
            )
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

        }

        Text(
            modifier = Modifier.padding(PaddingValues(top = 8.dp)),
            text = "Number of Tokens",
            style = MaterialTheme.typography.bodySmall
        )

        NumberEntry(
            value = event.count, onChanged = {
                onChanged(
                    event.copy(
                        count = it
                    )
                )
            })

        if (showExtendedButtons)
            DateTimeRow(
                modifier = Modifier.padding(PaddingValues(top = 8.dp)),
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
