package net.itsjustsomedude.tokens

import java.util.Calendar
import kotlin.math.max
import kotlin.math.pow

fun round(input: Double, roundTo: Double): Double {
    val i = 10.0.pow(roundTo);
    return Math.round(input * i) / i
}

fun round(input: Double, roundTo: Int): Double = round(input, roundTo.toDouble())

fun tval(startTime: Calendar, endTime: Calendar, tokenTime: Calendar, count: Int) = tval(
    (startTime.timeInMillis / 1000),
    (endTime.timeInMillis / 1000),
    (tokenTime.timeInMillis / 1000),
    count
);

fun tval(startTime: Long, endTime: Long, tokenTime: Long, count: Int): Double {
    val duration = (endTime - startTime).toDouble()
    val elapsed = (tokenTime - startTime).toDouble()

    val i = (1 - 0.9 * (elapsed / duration)).pow(4.0)
    val singleValue = max(i, 0.03)

    return singleValue * count
}

fun roundedTval(
    roundTo: Int,
    startTime: Calendar,
    endTime: Calendar,
    tokenTime: Calendar,
    count: Int
) =
    round(tval(startTime, endTime, tokenTime, count), roundTo)

fun roundedTval(roundTo: Int, startTime: Long, endTime: Long, tokenTime: Long, count: Int) =
    round(tval(startTime, endTime, tokenTime, count), roundTo)
