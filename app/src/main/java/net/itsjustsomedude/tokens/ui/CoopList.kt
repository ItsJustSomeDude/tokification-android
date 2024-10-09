package net.itsjustsomedude.tokens.ui

import android.text.format.DateFormat
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.db.Coop

@Composable
fun CoopList(
    modifier: Modifier = Modifier,
    coops: List<Coop>,
    onDelete: (id: Long, deleteEvents: Boolean) -> Unit,
    onSelect: (id: Long) -> Unit,
    listPre: @Composable (() -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (listPre != null)
            item { listPre() }

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

    val context = LocalContext.current

    val dateFormatter = remember {
        DateFormat.getDateFormat(context)
    }

    val timeFormatter = remember {
        DateFormat.getTimeFormat(context)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onclick(coop.id) },
                    onLongPress = { showDeleteDialog = true },
                )
            }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(6 / 12f)
                .fillMaxWidth()
        ) {
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = coop.name.ifBlank { "<No Name>" },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                text = coop.contract.ifBlank { "<No KevID>" },
                style = MaterialTheme.typography.bodySmall
            )
        }
        Column(
            modifier = Modifier.weight(2 / 12f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (coop.endTime == null) "-" else dateFormatter.format(coop.endTime.time),

                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (coop.sinkMode) "Sink" else "Normal",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Column(
            modifier = Modifier
                .weight(4 / 12f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = if (coop.endTime == null) "-" else timeFormatter.format(coop.endTime.time),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = coop.id.toString(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onTap = { onclick(coop.id) },
//                    onLongPress = { showDeleteDialog = true },
//                )
//            }
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
////                modifier = Modifier.weight(0.5f),
////                maxLines = 1,
////                overflow = TextOverflow.Ellipsis,
//                text = coop.name.ifBlank { "<No Name>" },
//                style = MaterialTheme.typography.bodyLarge,
//            )
//            Text(
////                modifier = Modifier.weight(0.5f),
////                maxLines = 1,
////                overflow = TextOverflow.Ellipsis,
//                text = if (coop.startTime == null) "?" else dateFormatter.format(coop.startTime.time),
//                style = MaterialTheme.typography.bodyLarge,
//            )
//        }
//        Row(
//            modifier = modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
////                modifier = Modifier.weight(0.3f),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//
//                text = coop.contract.ifBlank { "<No KevID>" },
//                style = MaterialTheme.typography.bodySmall,
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
////                modifier = Modifier.weight(0.3f),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//
//                text = if (coop.sinkMode) "Sink Mode" else "Normal Mode",
//                style = MaterialTheme.typography.bodySmall,
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
////                modifier = Modifier.weight(0.3f),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//
//                text = coop.id.toString(),
//                style = MaterialTheme.typography.bodySmall,
//            )
//        }
//    }

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