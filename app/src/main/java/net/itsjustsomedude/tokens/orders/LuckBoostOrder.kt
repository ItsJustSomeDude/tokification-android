package net.itsjustsomedude.tokens.orders

import net.itsjustsomedude.tokens.db.ExpandedCoop

class LuckBoostOrder : BoostOrder() {
	override fun arrange(data: ExpandedCoop): List<BoostOrderItem> {
		val sortedSentMap = data.tvalDelta.toList()
			.sortedByDescending { it.second }
			.toMap()

		val outputOrder = mutableListOf<BoostOrderItem>()

		for (entry in sortedSentMap) {
			val item = BoostOrderItem(
				playerName = entry.key,
				// TODO: Boost Order Stuff - Change this to pull from data.boostedPlayers
				tokensSent = data.tokensRec.containsKey(entry.key),
				boosted = false,
				playerDiscordName = "",
				playerDiscordId = "",
				sos = false,
				// TODO: Boost Order Stuff - Hardcoded Names ;)
				tokenRequest = when (entry.key) {
					"QuailMajeggstic" -> 7
					"Vipertgb" -> 7
					else -> 6
				},
				prefix = (data.tokensSent[entry.key] ?: 0).toString()
			)

			outputOrder.add(item)
		}

		return outputOrder
	}
}