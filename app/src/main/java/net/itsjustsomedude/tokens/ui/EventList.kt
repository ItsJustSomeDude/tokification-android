package net.itsjustsomedude.tokens.ui

import android.text.format.DateFormat
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.R
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.roundedTval
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListSheet(
    modifier: Modifier = Modifier,
    events: List<Event>,
    coop: Coop? = null,
    onDismissRequest: () -> Unit,
    onSelect: (id: Long) -> Unit,
    onDelete: (event: Event) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier) {
        EventList(
            events = events,
            onSelect = onSelect,
            onDelete = onDelete,
            coopStart = (coop?.startTime),
            coopEnd = (coop?.endTime),
        )
    }
}

@Composable
fun EventList(
    modifier: Modifier = Modifier,
    events: List<Event>,
    onSelect: (id: Long) -> Unit,
    onDelete: (event: Event) -> Unit,
    coopStart: Calendar? = null,
    coopEnd: Calendar? = null,
) {
    if (events.isEmpty()) {
        Text(
            text = "No Events",
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        )
    }

    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(events) { item ->
            EventListItem(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onSelect(item.id) },
                        onLongPress = { eventToDelete = item }
                    )
                },
                event = item,
                coopStart = coopStart,
                coopEnd = coopEnd
            )
            HorizontalDivider()
        }
    }

    if (eventToDelete != null)
        YesNoDialog(
            title = "Delete Event?",
            onDismissRequest = { eventToDelete = null },
            onNoClick = { eventToDelete = null },
            onYesClick = {
                onDelete(eventToDelete!!)
                eventToDelete = null
            }
        ) {
            Text(text = "Really delete this event?")
        }
}

@Composable
private fun EventListItem(
    modifier: Modifier = Modifier,
    event: Event,
    coopStart: Calendar? = null,
    coopEnd: Calendar? = null
) {
    val context = LocalContext.current
    val timeFormatter = remember {
        DateFormat.getTimeFormat(context)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = AnnotatedString.Builder().apply {
                    if (event.person.isBlank())
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("Nobody") }
                    else
                        append(event.person)

                    if (event.direction == Event.DIRECTION_SENT)
                        append(" Sent ")
                    else
                        append(" Received ")

                    if (event.count == 0)
                        append("a CR")
                    else
                        append("${event.count}")
                }.toAnnotatedString(),
                style = MaterialTheme.typography.bodyLarge,
            )

            if (coopStart != null && coopEnd != null && event.count != 0)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${roundedTval(3, coopStart, coopEnd, event.time, 1)} / ",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Icon(
                        painter = painterResource(R.drawable.offline_bolt),
                        contentDescription = "Token",
                        modifier = modifier.size(18.dp)
                    )
                }
        }
        Text(
            text = timeFormatter.format(event.time.time),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}