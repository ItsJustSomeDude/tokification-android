package net.itsjustsomedude.tokens.db;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class Converters {
	@TypeConverter
	public static Long fromCalendarToLong(Calendar calendar) {
		return calendar == null ? null : calendar.getTimeInMillis();
	}

	// Convert Long to Calendar
	@TypeConverter
	public static Calendar fromLongToCalendar(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar;
	}

	// Convert Integer to Boolean
	@TypeConverter
	public static Boolean fromIntegerToBoolean(Integer value) {
		return value == null ? null : value != 0;
	}

	// Convert Boolean to Integer
	@TypeConverter
	public static Integer fromBooleanToInteger(Boolean value) {
		return value == null ? null : value ? 1 : 0;
	}
}
