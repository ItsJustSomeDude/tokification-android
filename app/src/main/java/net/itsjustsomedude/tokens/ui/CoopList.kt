package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.db.Coop

@Composable
fun CoopList(
    modifier: Modifier = Modifier,
    coops: List<Coop>,
    onDelete: (id: Long, deleteEvents: Boolean) -> Unit,
    onSelect: (id: Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(coops) { item ->
            CoopListItem(
                coop = item,
                onclick = onSelect,
                onDelete = onDelete
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun CoopListItem(
    modifier: Modifier = Modifier,
    coop: Coop,
    onDelete: (id: Long, deleteEvents: Boolean) -> Unit,
    onclick: (id: Long) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onclick(coop.id) },
                    onLongPress = { showDeleteDialog = true },
                )
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = coop.name,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = if (false) "..." else "...",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = coop.contract,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = if (coop.sinkMode) "Sink Mode" else "Normal Mode",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = coop.id.toString(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    if (showDeleteDialog) {
        var deleteEvents by remember { mutableStateOf(false) }

        YesNoDialog(
            title = "Delete Coop '${coop.name}'?",
            onDismissRequest = { showDeleteDialog = false },
            onNoClick = { showDeleteDialog = false },
            onYesClick = {
                showDeleteDialog = false

                onDelete(coop.id, deleteEvents)
            }
        ) {
            Text(text = "Really delete this coop?")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Delete Events")
                Checkbox(
                    enabled = false,
                    checked = deleteEvents,
                    onCheckedChange = { deleteEvents = it }
                )
            }
        }
    }
}