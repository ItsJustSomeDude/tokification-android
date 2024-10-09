package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import org.koin.mp.KoinPlatform.getKoin

abstract class Report {
    abstract fun generate(data: ReportData): String

    fun generate(coop: Coop, events: List<Event>): String {
        val data = ReportData(coop, events)
        return generate(data)
    }

    suspend fun generate(coopId: Long): String {
        val coopRepo: CoopRepository = getKoin().get()
        val eventRepo: EventRepository = getKoin().get()

        val coop = coopRepo.getCoopDirect(coopId) ?: return "Unknown Coop"
        val events = eventRepo.listEventsDirect(coop.name, coop.contract)

        return generate(coop, events)
    }
}
