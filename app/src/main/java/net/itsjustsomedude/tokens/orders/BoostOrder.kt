package net.itsjustsomedude.tokens.orders

import net.itsjustsomedude.tokens.db.ExpandedCoop
import net.itsjustsomedude.tokens.db.ExpandedCoopRepository
import org.koin.mp.KoinPlatform.getKoin

abstract class BoostOrder {
	abstract fun arrange(data: ExpandedCoop): List<BoostOrderItem>

	suspend fun getBoostOrder(coopId: Long): List<BoostOrderItem> {
		val coopRepo: ExpandedCoopRepository = getKoin().get()
		val coop = coopRepo.getExpandedCoop(coopId) ?: return emptyList()

		return arrange(coop)
	}
}