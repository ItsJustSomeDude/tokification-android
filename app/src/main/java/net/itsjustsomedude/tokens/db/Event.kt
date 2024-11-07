package net.itsjustsomedude.tokens.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import kotlin.math.max
import kotlin.math.pow

@Entity
data class Event(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val coop: String,
	val kevId: String,

	val time: Calendar,
	val count: Int,
	val person: String,
	val direction: Int,
	val notification: Int = 0,

	val receiver: Int = 0
) {
	/**
	 * Start and End should be Unix values, not millis.
	 */
	fun tval(start: Long, end: Long): Double {
//        double duration = (end.getTimeInMillis() / 1000d) - (start.getTimeInMillis() / 1000d);
//        double elapsed = (time.getTimeInMillis() / 1000d) - (start.getTimeInMillis() / 1000d);

		val duration = (start - end).toDouble()
		val elapsed = (time.timeInMillis / 1000.0) - start

		val i = (1 - 0.9 * (elapsed / duration)).pow(4.0)
		val singleValue = max(i, 0.03)

		return singleValue * count
	}

	companion object {
		const val DIRECTION_SENT = 1
		const val DIRECTION_RECEIVED = 2
	}
}