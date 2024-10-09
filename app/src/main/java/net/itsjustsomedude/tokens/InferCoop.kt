package net.itsjustsomedude.tokens

import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import org.koin.mp.KoinPlatform.getKoin

// This should...
// 1. Auto determine Coop/KevID if either of these are null.
// 2. Auto determine Player list from Events list.
// 3. Auto determine Start Time based on time of first event.

fun inferCoopValues(coop: Coop, events: List<Event>): Coop {
    val eventPlayers = mutableListOf<String>()
    for (ev in events) {
        eventPlayers.add(ev.person)
    }
    // TODO: Do this better.
    eventPlayers.remove("Sink")
    val newPlayerList = eventPlayers.union(coop.players).toList()
    println("Inferred player list: $newPlayerList")

    val newStart =
        coop.startTime
            ?: if (events.isNotEmpty())
                events[0].time
            else
                null

    return coop.copy(
        players = newPlayerList,
        startTime = newStart
    )
}

//suspend fun inferCoopName(coop: Coop): Coop {
//    var newCoopName = coop.name
//    var newKevId = coop.contract
//
//    if (newKevId.isBlank() && newCoopName.isNotBlank()) {
//        println("Attempting to determine KevID.")
//        events.firstOrNull { it.coop == newCoopName }?.let {
//            newKevId = it.kevId
//        }
//    } else if (newKevId.isNotBlank() && newCoopName.isBlank()) {
//        println("Attempting to determine Coop Name")
//        events.firstOrNull { it.kevId == newKevId }?.let {
//            newCoopName = it.coop
//        }
//    }
//}

suspend fun inferCoopValues(id: Long): Coop {
    // TODO: Usages of getKoin()
    val coopRepo: CoopRepository = getKoin().get()
    val eventRepo: EventRepository = getKoin().get()

    val coop = coopRepo.getCoopDirect(id) ?: throw Error("No Coop Found!")
    val events = eventRepo.listEventsDirect(coop.name, coop.contract)

    return inferCoopValues(coop, events)
}

suspend fun updateInferredCoopValues(id: Long) {
    // TODO: Usages of getKoin()
    val coopRepo: CoopRepository = getKoin().get()

    try {
        val newCoop = inferCoopValues(id)
        coopRepo.update(newCoop)
    } catch (_: Error) {
    }
}

// Probably Unused?
suspend fun updateInferredCoopValues(coopId: Long, event: Event) {
    // TODO: Usages of getKoin()
    val coopRepo: CoopRepository = getKoin().get()

    coopRepo.getCoopDirect(coopId)?.let {
        val newCoop = inferCoopValues(it, listOf(event))

        coopRepo.update(newCoop)
    }
}

suspend fun updateInferredCoopValues(event: Event) {
    // TODO: Usages of getKoin()
    val coopRepo: CoopRepository = getKoin().get()

    coopRepo.getCoopByNameDirect(event.coop, event.kevId)?.let {
        val newCoop = inferCoopValues(it, listOf(event))
        println("Inferring...")

        coopRepo.update(newCoop)
    }
}