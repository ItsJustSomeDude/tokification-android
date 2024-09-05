package net.itsjustsomedude.tokens.db

import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar

object Converters {
    @TypeConverter
    fun fromCalendarToLong(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    // Convert Long to Calendar
    @TypeConverter
    fun fromLongToCalendar(timestamp: Long?): Calendar? {
        if (timestamp == null) {
            return null
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar
    }

    // Convert Integer to Boolean
    @TypeConverter
    fun fromIntegerToBoolean(value: Int?): Boolean? {
        return if (value == null) null else value != 0
    }

    // Convert Boolean to Integer
    @TypeConverter
    fun fromBooleanToInteger(value: Boolean?): Int? {
        return if (value == null) null else if (value) 1 else 0
    }

    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString<List<String>>(it) }
    }
}
