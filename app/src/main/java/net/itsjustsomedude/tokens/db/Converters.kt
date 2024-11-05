package net.itsjustsomedude.tokens.db

import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar

object Converters {
    @TypeConverter
    fun fromCalendarToLong(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun fromLongToCalendar(timestamp: Long?): Calendar? {
        if (timestamp == null)
            return null

        return Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
    }

    @TypeConverter
    fun fromIntegerToBoolean(value: Int?): Boolean? {
        return if (value == null) null else value != 0
    }

    @TypeConverter
    fun fromBooleanToInteger(value: Boolean?): Int? {
        return if (value == null) null else if (value) 1 else 0
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString<List<String>>(it) }
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun toStringIntMap(value: Map<String, Int>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun fromStringIntMap(value: String?): Map<String, Int>? {
        return value?.let { Json.decodeFromString(it) }
    }
}
