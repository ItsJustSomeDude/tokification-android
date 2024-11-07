package net.itsjustsomedude.tokens.reader

import android.app.Notification
import android.util.Log
import net.itsjustsomedude.tokens.db.Event
import java.util.regex.Pattern

private const val TAG = "NoteConvert"

private val personCoopRegex =
	Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$")
private val tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)")
private val ALLOWED_PACKAGES = listOf(
	"com.auxbrain.egginc",
	"net.itsjustsomedude.tokens",
	"net.itsjustsomedude.tokens.debug"
)

fun ShortNotification.toEvent(): Event? {
	if (!ALLOWED_PACKAGES.contains(packageName)) return null
	if ((flags and Notification.FLAG_GROUP_SUMMARY) != 0) return null
	if (!title.contains("Gift Received")) return null

	val matches = personCoopRegex.matcher(text)
	if (!matches.lookingAt() || matches.groupCount() < 2) {
		Log.e(TAG, "Person/Coop Regex didn't match for notification content: $text")
		return null
	}

	val person = matches.group(1)!!
	val coopName = matches.group(2)!!

	if (title.contains("🐣")) {
		Log.i(TAG, "Processing CR")
		return Event(
			coop = coopName,
			kevId = group,
			person = person,
			count = 0,      // Count of 0 indicates CR.
			time = time,
			direction = Event.DIRECTION_SENT,
			notification = id
		)
	}

	val amount: Int
	if (text.contains("has sent you a Boost Token")) amount = 1
	else {
		try {
			val countMatch = tokenCountRegex.matcher(text)

			if (!countMatch.find() || countMatch.group(1) == null) {
				Log.e(
					TAG,
					"Count Regex didn't match for notification content: $text"
				)
				return null
			}

			amount = countMatch.group(1)!!.toInt()
		} catch (err: NumberFormatException) {
			Log.e(TAG, "Bad number in note: $text")
			return null
		}
	}

	return Event(
		coop = coopName,
		kevId = group,
		person = person,
		count = amount,
		time = time,
		direction = Event.DIRECTION_SENT,
		notification = id
	)
}