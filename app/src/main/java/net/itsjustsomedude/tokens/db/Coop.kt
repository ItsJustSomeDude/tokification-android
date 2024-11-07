package net.itsjustsomedude.tokens.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class Coop(
	@PrimaryKey(autoGenerate = true)
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
	val playerTokenAmounts: Map<String, Int> = emptyMap()
) {
	companion object {
		// TODO: Should these be moved to another class?
		const val BOOST_ORDER_UNKNOWN = 0
		const val BOOST_ORDER_LUCK = 1
		const val BOOST_ORDER_EB = 2
	}
}
