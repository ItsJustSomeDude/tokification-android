package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.ExpandedCoop
import net.itsjustsomedude.tokens.db.ExpandedCoopRepository
import net.itsjustsomedude.tokens.db.expand
import org.koin.mp.KoinPlatform.getKoin

abstract class Report {
    abstract fun generate(data: ExpandedCoop): String

    fun generate(coop: Coop, events: List<Event>): String {
        val data = coop.expand(events)
        return generate(data)
    }

    suspend fun generate(coopId: Long): String {
        val coopRepo: ExpandedCoopRepository = getKoin().get()

        val coop = coopRepo.getExpandedCoop(coopId) ?: return "Unknown Coop"

        return generate(coop)
    }
}
