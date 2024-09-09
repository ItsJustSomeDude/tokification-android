package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.models.EventListViewModel

@Composable
fun EventList(
    coop: String,
    kevId: String,
    modifier: Modifier = Modifier,
    model: EventListViewModel = viewModel(),
    onSelect: (id: Long) -> Unit,
) {
    LaunchedEffect(key1 = coop, key2 = kevId) {
        model.setSelectedCoop(coop, kevId)
    }

    val items by model.events.observeAsState(emptyList())

    if (items.isEmpty()) {
        Text(
            text = "No Events",
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(items) { item ->
            EventListItem(item, onclick = onSelect)
            HorizontalDivider()
        }
    }
}

@Composable
private fun EventListItem(
    event: Event,
    onclick: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onclick(event.id)
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${event.person} ${if (event.direction == Event.DIRECTION_SENT) "Sent" else "Received"} ${event.count}",
                style = MaterialTheme.typography.bodyLarge,
            )
//            Text(
//                text = if (false) "..." else "...",
//                style = MaterialTheme.typography.bodyLarge,
//            )
        }
//        Row(
//            modifier = modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = coop.contract,
//                style = MaterialTheme.typography.bodySmall,
//            )
//            Text(
//                text = if (coop.sinkMode) "Sink Mode" else "Normal Mode",
//                style = MaterialTheme.typography.bodySmall,
//            )
//            Text(
//                text = coop.id.toString(),
//                style = MaterialTheme.typography.bodySmall,
//            )
//        }
    }
}