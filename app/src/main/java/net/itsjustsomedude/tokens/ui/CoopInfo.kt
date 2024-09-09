package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.ReportBuilder
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.models.CoopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCoop(
    coopId: Long?,
    modifier: Modifier = Modifier,
    model: CoopViewModel = viewModel()
) {
    LaunchedEffect(key1 = coopId) {
        model.setSelectedCoop(coopId)
    }

    var showEventListSheet by remember { mutableStateOf(false) }
    var showEventEditDialog by remember { mutableStateOf(false) }
    var event by remember { mutableLongStateOf(0) }

    val modelCoop by model.coop.observeAsState()
    modelCoop?.let { coop ->
        CoopInfo(
            coop = coop,
            onEventsClicked = {
                showEventListSheet = true
            },
            onSendClicked = {
                showEventEditDialog = true
            },
            onChanged = {
                model.update(it)
            }
        )

        if (showEventListSheet)
            ModalBottomSheet(onDismissRequest = { showEventListSheet = false }) {
                EventList(
                    coop = coop.name,
                    kevId = coop.contract,
                    onSelect = {
                        event = it
                        showEventListSheet = false
                    }
                )
            }

        if (showEventEditDialog)
            AlertDialog(
                onDismissRequest = {
                    event = 0
                    showEventEditDialog = false
                },
                confirmButton = {
                    Button(onClick = {
                        event = 0
                        showEventEditDialog = false
                    }) {
                        Text("Save")
                    }
                },
                text = {
                    EventEdit(eventId = event)
                })

    } ?: Text("Invalid Coop Selected.")
}

@Composable
fun CoopInfo(
    coop: Coop,
    onEventsClicked: () -> Unit,
    onSendClicked: () -> Unit,
    onChanged: (coop: Coop) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Column {
        Row {
            Column {
                if (coop.name.isNullOrBlank())
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

                if (coop.contract.isNullOrBlank())
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
                IconButton(onClick = {
                    showEditDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Coop"
                    )
                }
            }
        }
        Row {
            Button(onClick = onEventsClicked) {
                Text(text = "Edit Events")
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
                coop.startTime = it
                onChanged(coop)
            }
        )

        DateTimeRow(
            dateLabel = "End Date",
            timeLabel = "End Time",
            unsetText = "Set",
            date = coop.endTime,
            onChange = {
                coop.endTime = it
                onChanged(coop)
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
                coop.sinkMode = it
                onChanged(coop)
            })

            Text(
                text = if (coop.sinkMode) "Sink Mode" else "Normal Mode",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row {
            if (coop.sinkMode) {
                Button(onClick = {}) {
                    Text("Generate")
                }
            } else {
                ReportBuilder(coop)
                Text("Report...")
            }
        }
    }

    if (showEditDialog) {
        val regex = remember { Regex("^[a-z0-9\\-]*\$") }

        TwoTextFieldDialog(
            onDismiss = { showEditDialog = false },
            onConfirm = { coopName, kevId ->
                coop.name = coopName
                coop.contract = kevId

                onChanged(coop)
                showEditDialog = false
            },
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
}
