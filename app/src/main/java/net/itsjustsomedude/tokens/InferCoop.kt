package net.itsjustsomedude.tokens

import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.Event

// This should...
// 1. Auto determine Coop/KevID if either of these are null.
// 2. Auto determine Player list from Events list.
// 3. Auto determine Start Time based on time of first event.

fun inferCoopValues(coop: Coop, events: List<Event>): Coop {
    var newCoopName = coop.name
    var newKevId = coop.contract

    if (newKevId.isBlank() && newCoopName.isNotBlank()) {
        println("Attempting to determine KevID.")
    } else if (newKevId.isNotBlank() && newCoopName.isBlank()) {
        println("Attempting to determine Coop Name")
    }

    val eventPlayers = mutableListOf<String>()
    for (ev in events) {
        eventPlayers.add(ev.person)
    }
    val newPlayerList = eventPlayers.union(coop.players).toList()

    val newStart =
        coop.startTime
            ?: if (events.isNotEmpty())
                events[0].time
            else
                null

    return coop.copy(
        name = newCoopName,
        contract = newKevId,
        players = newPlayerList,
        startTime = newStart
    )
}
