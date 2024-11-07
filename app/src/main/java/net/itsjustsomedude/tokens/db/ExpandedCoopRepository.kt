package net.itsjustsomedude.tokens.db

class ExpandedCoopRepository(
	private val coopDao: CoopDao,
	private val eventDao: EventDao
) {
	suspend fun getExpandedCoop(coopId: Long): ExpandedCoop? {
		val coop = coopDao.getCoop(coopId) ?: return null
		val events = eventDao.listEvents(coop.name, coop.contract)

		return coop.expand(events)
	}
}