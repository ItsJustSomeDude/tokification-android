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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.models.CoopViewModel
import net.itsjustsomedude.tokens.models.provideDialogController
import net.itsjustsomedude.tokens.reports.LuckBoostReport
import net.itsjustsomedude.tokens.reports.SelfReport

@Composable
fun EditCoop(
    coopId: Long,
    model: CoopViewModel = CoopViewModel.provide(coopId)
) {
    var showEventList by model.showEventList
    var showNameEdit by model.showNameEdit

    val modelCoop by model.coop.observeAsState()
    val selectedEventId by model.selectedEventId.observeAsState()
    val events by model.events.observeAsState(emptyList())

    val eventDialog = provideDialogController()

    modelCoop?.let { coop ->
        CoopInfo(
            coop = coop,
            numEvents = events.size,
            reportText = if (coop.sinkMode)
                LuckBoostReport().generate(coop, events)
            else
                SelfReport().generate(coop, events),

            onEventsClicked = {
                showEventList = true
            },
            onSendClicked = {
                // Create new event.
                // TODO: Add keys to the CreateEventModel, as this breaks when switching coops then clicking send!
                model.selectEvent(null)
                eventDialog.show()
            },
            onEditClicked = {
                showNameEdit = true
            },
            onReportClicked = {
                model.copySinkReport()
            },
            onDetailedReportClicked = {
                model.copyDetailedReport()
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
                    model.selectEvent(it)

                    showEventList = false
                    eventDialog.show()
                },
                onDelete = {
                    model.selectEvent(null)
                    model.deleteEvent(it)
                }
            )

        val eventDialogVisible by eventDialog.visible.collectAsState()
        if (eventDialogVisible)
            EventEditDialog(
                dialogKey = eventDialog.key,
                coopId = coop.id,
                eventId = selectedEventId,
                onDismiss = { eventDialog.hide() }
            )

        if (showNameEdit)
            CoopNameEditDialog(
                initialCoop = coop.name,
                initialKevId = coop.contract,
                onDismiss = { showNameEdit = false },
                onConfirm = { coopName, kevId ->
                    model.update(
                        coop.copy(
                            name = coopName,
                            contract = kevId
                        )
                    )

                    showNameEdit = false
                },
            )
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
    onDetailedReportClicked: () -> Unit,
    onChanged: (coop: Coop) -> Unit,
) {
    Column(modifier = modifier) {
        Row {
            Column(
                Modifier.weight(1f, false)
            ) {
                if (coop.name.isBlank())
                    Text(
                        text = "No coop name",
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic
                    )
                else
                    Text(
                        text = coop.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
            }
            IconButton(
                onClick = onEditClicked
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Coop"
                )
            }
        }

        if (coop.name.isBlank() || coop.name.isBlank())
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "Enter Name and KevID before recording events!",
                    color = MaterialTheme.colorScheme.error
                )
            }

        Row {
            Button(onClick = onEventsClicked) {
                Text(text = "Edit Events ($numEvents)")
            }
            IconButton(
                enabled = coop.name.isNotBlank() && coop.name.isNotBlank(),
                onClick = onSendClicked
            ) {
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

        if (coop.sinkMode)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onReportClicked) {
                    Text("Copy Report")
                }
                if (coop.startTime != null && coop.endTime != null)
                    Button(onClick = onDetailedReportClicked) {
                        Text("Copy Detailed Report")
                    }
            }

        Text(reportText)
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

@Preview(showBackground = true)
@Composable
fun PreviewCoopInfo() {
    CoopInfo(coop = Coop(),
        onSendClicked = {},
        onEditClicked = {},
        onEventsClicked = {},
        onReportClicked = {},
        onDetailedReportClicked = {},
        onChanged = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNullCoopInfo() {
    CoopInfoSkeleton()
}
