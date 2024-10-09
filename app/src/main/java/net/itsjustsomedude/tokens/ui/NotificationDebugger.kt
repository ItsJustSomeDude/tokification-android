package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.models.NotificationDebuggerViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationDebugger(
    modifier: Modifier = Modifier,
    model: NotificationDebuggerViewModel = koinViewModel()
) {
    var player by model.player
    var coop by model.coop
    var kevId by model.kevId

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text("Send Fake Notification")

        OutlinedTextField(
            value = player,
            onValueChange = { player = it },
            singleLine = true,
            label = {
                Text("Player")
            }
        )

        OutlinedTextField(
            value = coop,
            onValueChange = { coop = it },
            singleLine = true,
            label = {
                Text("Co-op")
            }
        )

        OutlinedTextField(
            value = kevId,
            onValueChange = { kevId = it },
            singleLine = true,
            label = {
                Text("KevID")
            }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                if (player.isBlank() || kevId.isBlank() || coop.isBlank()) return@Button

                model.sendNotification(isCR = false)
            }) {
                Text("Send Token")
            }

            Button(onClick = {
                if (player.isBlank() || kevId.isBlank() || coop.isBlank()) return@Button

                model.sendNotification(isCR = true)
            }) {
                Text("Send CR")
            }
        }
    }
}

@Composable
fun NotificationDebuggerDialog(modifier: Modifier = Modifier, onDismissRequest: () -> Unit) {
    AlertDialog(
        title = {
            Text("Notification Debugger")
        },
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("Close")
            }
        },
        text = {
            NotificationDebugger()
        })
}

@Preview(showBackground = true)
@Composable
fun PreviewNotificationDebugger() {
    NotificationDebugger()
}