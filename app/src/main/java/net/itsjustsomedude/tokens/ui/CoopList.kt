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
    onDelete: (coop: Coop, deleteEvents: Boolean) -> Unit,
    onSelect: (id: Long) -> Unit,
    listPre: @Composable (() -> Unit)? = null
) {
    var coopToDelete by remember { mutableStateOf<Coop?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (listPre != null)
            item { listPre() }

        items(coops) { item ->
            CoopListItem(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onSelect(item.id) },
                            onLongPress = { coopToDelete = item }
                        )
                    },
                coop = item,
            )
            HorizontalDivider()
        }
    }

    if (coopToDelete != null) {
        var deleteEvents by remember { mutableStateOf(false) }

        YesNoDialog(
            title = "Delete Coop '${coopToDelete!!.name}'?",
            onDismissRequest = { coopToDelete = null },
            onNoClick = { coopToDelete = null },
            onYesClick = {
                onDelete(coopToDelete!!, deleteEvents)

                coopToDelete = null
            }
        ) {
            Text(text = "Really delete this coop?")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Delete Events")
                Checkbox(
                    checked = deleteEvents,
                    onCheckedChange = { deleteEvents = it }
                )
            }
        }
    }

}

@Composable
private fun CoopListItem(
    modifier: Modifier = Modifier,
    coop: Coop,
) {
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
}