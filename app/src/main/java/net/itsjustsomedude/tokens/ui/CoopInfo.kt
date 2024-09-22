package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.models.CoopViewModel
import net.itsjustsomedude.tokens.models.SelfReport
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditCoop(
    coopId: Long?,
    model: CoopViewModel = koinViewModel(
        parameters = { parametersOf(coopId) },
        key = coopId.toString()
    )
) {
    var showEventList by model.showEventList
    var showEventEdit by model.showEventEdit
    var showNameEdit by model.showNameEdit

    val modelCoop by model.coop.observeAsState()
    val selectedEvent by model.selectedEvent.observeAsState()
    val events by model.events.observeAsState(emptyList())

    println("The Coop Outside: $modelCoop with ID $coopId")

    modelCoop?.let { coop ->
        println("The Coop: $coop with ID $coopId")

        CoopInfo(
            coop = coop,
            numEvents = events.size,
            reportText = SelfReport().generate(coop, events),
            onEventsClicked = {
                showEventList = true
            },
            onSendClicked = {
                model.createEvent(
                    count = if (coop.sinkMode) 6 else 2
                )
                showEventEdit = true
            },
            onEditClicked = {

                showNameEdit = true
            },
            onReportClicked = {
                model.copySinkReport()
            },
            onChanged = {
                model.update(it)
                model.postActions()
            }
        )

        if (showEventList)
            EventListSheet(
                events = events,
                coop = coop,
                onDismissRequest = {
                    showEventList = false
                },
                onSelect = {
                    model.loadEvent(it)

                    showEventList = false
                    showEventEdit = true
                })

        if (showEventEdit)
            EventEditDialog(
                event = selectedEvent,
                players = coop.players,
                onDismissRequest = {
                    showEventEdit = false
                },
                onDoneClicked = {
                    model.saveSelectedEvent()
                    showEventEdit = false
                },
                onChanged = {
                    model.updateSelectedEvent(it)
                }
            )

        if (showNameEdit)
            CoopNameEditDialog(
                coop = coop,
                onDismiss = { showNameEdit = false },
                onChanged = { coopName, kevId ->
                    model.update(
                        coop.copy(
                            name = coopName,
                            contract = kevId
                        )
                    )

                    showNameEdit = false
                })

    } ?: CoopInfoSkeleton()
}

@Composable
fun CoopInfo(
    modifier: Modifier = Modifier,
    coop: Coop,
    numEvents: Int = 0,
    reportText: String = "",
    onEventsClicked: () -> Unit,
    onSendClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onReportClicked: () -> Unit,
    onChanged: (coop: Coop) -> Unit,
) {
    Column(modifier = modifier) {
        Row {
            Column {
                if (coop.name.isBlank())
                    Text(
                        text = "No coop name",
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic
                    )
                else
                    Text(
                        text = coop.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                if (coop.contract.isBlank())
                    Text(
                        text = "No contract id",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                else
                    Text(
                        text = coop.contract,
                        style = MaterialTheme.typography.bodySmall
                    )
            }
            Column {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Coop"
                    )
                }
            }
        }
        Row {
            Button(onClick = onEventsClicked) {
                Text(text = "Edit Events ($numEvents)")
            }
            IconButton(onClick = onSendClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Tokens"
                )
            }
        }

        DateTimeRow(
            dateLabel = "Start Date",
            timeLabel = "Start Time",
            unsetText = "Set",
            date = coop.startTime,
            onChange = {
                onChanged(
                    coop.copy(
                        startTime = it
                    )
                )
            }
        )

        DateTimeRow(
            dateLabel = "End Date",
            timeLabel = "End Time",
            unsetText = "Set",
            date = coop.endTime,
            onChange = {
                onChanged(
                    coop.copy(
                        endTime = it
                    )
                )
            }
        )

        if (coop.startTime != null && coop.endTime != null && coop.startTime.timeInMillis > coop.endTime.timeInMillis)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "End time is before start time!",
                    color = MaterialTheme.colorScheme.error
                )
            }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Switch(checked = coop.sinkMode, onCheckedChange = {
                onChanged(
                    coop.copy(
                        sinkMode = it
                    )
                )
            })

            Text(
                text = if (coop.sinkMode) "Sink Mode" else "Normal Mode",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row {
            if (coop.sinkMode) {
                Button(onClick = onReportClicked) {
                    Text("Generate")
                }
            } else {
                Text(reportText)
            }
        }
    }
}

@Composable
fun CoopInfoSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row {
            Column {
                Box(
                    Modifier
                        .size(100.dp, 28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .skeletonColors()
                )

                Box(
                    Modifier
                        .size(100.dp, 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .skeletonColors()
                )
            }
            Column {
                IconButton(onClick = {}, enabled = false) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Coop"
                    )
                }
            }
        }
        Row {
            Box(
                Modifier
                    .size(116.dp, 40.dp)
                    .clip(RoundedCornerShape(50))
                    .skeletonColors()
            )

            IconButton(onClick = {}, enabled = false) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Tokens"
                )
            }
        }

        DateTimeRowSkeleton(
            dateLabel = "Start Date",
            timeLabel = "Start Time",
        )

        DateTimeRowSkeleton(
            dateLabel = "End Date",
            timeLabel = "End Time",
        )

        Box(
            Modifier
                .padding(vertical = 4.dp)
                .size(52.dp, 32.dp)
                .clip(RoundedCornerShape(50))
                .skeletonColors()
        )
    }

}

@Composable
fun CoopNameEditDialog(
    coop: Coop,
    onDismiss: () -> Unit,
    onChanged: (name: String, kevId: String) -> Unit,
) {
    val regex = remember { Regex("^[a-z0-9\\-]*\$") }

    TwoTextFieldDialog(
        onDismiss = onDismiss,
        onConfirm = onChanged,
        title = "Edit Coop",
        //TODO!
        text = "Note that this will... something, idk.\nIf one of these is left blank it will be auto-determined.",
        field1Label = "Coop Name",
        field1Initial = coop.name,
        field2Label = "Contract ID (KevID)",
        field2Initial = coop.contract,
        valueSetter = {
            val a = it
                .lowercase()
                .replace(" ", "-")
                .replace("_", "-")
            if (a.matches(regex)) {
                a
            } else null
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCoopInfo() {
    CoopInfo(coop = Coop(),
        onSendClicked = {},
        onEditClicked = {},
        onEventsClicked = {},
        onReportClicked = {},
        onChanged = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNullCoopInfo() {
    CoopInfoSkeleton()
}
