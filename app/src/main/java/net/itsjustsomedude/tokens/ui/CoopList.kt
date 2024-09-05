package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.models.CoopListViewModel

@Composable
fun CoopList(
    modifier: Modifier = Modifier,
    model: CoopListViewModel = viewModel(),
    onSelect: (id: Long) -> Unit,
) {
    val items by model.coops.observeAsState(emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(items) { item ->
            CoopListItem(item, onclick = onSelect)
            HorizontalDivider()
        }
    }

}

@Composable
private fun CoopListItem(coop: Coop, onclick: (id: Long) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onclick(coop.id)
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
}