package net.itsjustsomedude.tokens.db

import net.itsjustsomedude.tokens.db.Coop.Companion.BOOST_ORDER_UNKNOWN
import net.itsjustsomedude.tokens.roundedTval
import net.itsjustsomedude.tokens.tval
import java.util.Calendar

@Suppress("MemberVisibilityCanBePrivate")
data class ExpandedCoop(
	val id: Long = 0,

	val name: String = "",
	val contract: String = "",
	val startTime: Calendar? = null,
	val endTime: Calendar? = null,
	val sinkMode: Boolean = false,
	val boostOrder: Int = BOOST_ORDER_UNKNOWN,

	val players: List<String> = emptyList(),
	val sink: String = "",

	/** Used to lock a players into certain boost order positions. */
	val playerPositionOverrides: Map<String, Int> = emptyMap(),

	/** Used to have certain players following a different boost order than the default. */
	val playerOrderOverrides: Map<String, Int> = emptyMap(),

	/** Overrides the number of tokens each player wants. */
	val playerTokenAmounts: Map<String, Int> = emptyMap(),

	val events: List<Event> = emptyList()
) {
	val isStartEstimated = startTime == null
	val isEndEstimated = endTime == null

	val nowEpoch = Calendar.getInstance().timeInMillis / 1000L

	val startEpoch = startTime?.let {
		it.timeInMillis / 1000L
	} ?: (nowEpoch % 3600)

	val endEpoch = endTime?.let {
		it.timeInMillis / 1000L
	} ?: (startEpoch + 12 * 60 * 60)

	val isEnded = nowEpoch > endEpoch

	val tvalNow = roundedTval(4, startEpoch, endEpoch, nowEpoch, 1)
	val tval30Mins = roundedTval(4, startEpoch, endEpoch, nowEpoch + 30 * 60, 1)
	val tval60Mins = roundedTval(4, startEpoch, endEpoch, nowEpoch + 60 * 60, 1)

	val tokensSent = mutableMapOf<String, Int>()
	val tokensRec = mutableMapOf<String, Int>()
	val tvalSent = mutableMapOf<String, Double>()
	val tvalRec = mutableMapOf<String, Double>()

	val tvalDelta: Map<String, Double>

	val selfTokensSent: Int
	val selfTokensReceived: Int
	val selfTvalSent: Double
	val selfTvalReceived: Double

	val selfTvalDelta: Double

	init {
		var selfTokensSent = 0
		var selfTokensReceived = 0
		var selfTvalSent = 0.0
		var selfTvalReceived = 0.0

		for (event in events) with(event) {
			val evTime: Long = time.timeInMillis / 1000L
			val tv = tval(startEpoch, endEpoch, evTime, count)

			if (direction == Event.DIRECTION_SENT) {
				tokensSent[person] = (tokensSent[person] ?: 0) + count
				tvalSent[person] = (tvalSent[person] ?: 0.0) + tv

				selfTokensReceived += count
				selfTvalReceived += tv
			} else {
				tokensRec[person] = (tokensRec[person] ?: 0) + count
				tvalRec[person] = (tvalRec[person] ?: 0.0) + tv

				selfTokensSent += count
				selfTvalSent += tv
			}
		}

		selfTvalDelta = selfTvalSent - selfTvalReceived

		tvalDelta = players.associateWith { player ->
			(tvalSent[player] ?: 0.0) - (tvalRec[player] ?: 0.0)
		}

		this.selfTokensSent = selfTokensSent
		this.selfTokensReceived = selfTokensReceived
		this.selfTvalSent = selfTvalSent
		this.selfTvalReceived = selfTvalReceived
	}

//	val coop
//		get() = Coop(
//			id,
//			name,
//			contract,
//			startTime,
//			endTime,
//			sinkMode,
//			boostOrder,
//			players,
//			sink,
//			playerPositionOverrides,
//			playerOrderOverrides,
//			playerTokenAmounts
//		)
}

fun Coop.expand(events: List<Event>) = ExpandedCoop(
	id,
	name,
	contract,
	startTime,
	endTime,
	sinkMode,
	boostOrder,
	players,
	sink,
	playerPositionOverrides,
	playerOrderOverrides,
	playerTokenAmounts,
	events
)
