package net.itsjustsomedude.tokens;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Database {
	private static final String TAG = "Database";

	private final DatabaseHelper dbHelper;
	private final Context context;
	private final SQLiteDatabase database;

	public Database(Context c) {
		context = c;
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean eventExists(int eventNote) {
		Cursor test = database.query(
				DatabaseHelper.EVENTS_TABLE,
				new String[]{DatabaseHelper.EVENT_NOTE_ID},
				DatabaseHelper.EVENT_NOTE_ID + " = " + eventNote,
				null, null, null, null, "1"
		);
		boolean exists = test.getCount() > 0;
		test.close();
		return exists;
	}

	public Event createEvent(String coop, String group, Calendar time, int count, String direction, String person) {
		return createEvent(coop, group, time, count, direction, person, 0);
	}

	public Event createEvent(String coop, String group, Calendar time, int count, String direction, String person, int notification) {
		long t = time.getTimeInMillis() / 1000L;

		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.EVENT_COOP, coop);
		cv.put(DatabaseHelper.EVENT_GROUP, group);
		cv.put(DatabaseHelper.EVENT_TIME, t);
		cv.put(DatabaseHelper.EVENT_COUNT, count);
		cv.put(DatabaseHelper.EVENT_DIR, direction);
		cv.put(DatabaseHelper.EVENT_PERSON, person);
		cv.put(DatabaseHelper.EVENT_NOTE_ID, notification);

		long newId = database.insert(
				DatabaseHelper.EVENTS_TABLE,
				null,
				cv
		);

		return new Event(newId, coop, group, time, count, person, direction, notification);
	}

	public Coop createCoop() {
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COOP_NAME, "New Coop");
		cv.put(DatabaseHelper.START_TIME, 0);
		cv.put(DatabaseHelper.END_TIME, 0);
		cv.put(DatabaseHelper.COOP_SINK_MODE, false);

		long newId = database.insert(
				DatabaseHelper.COOPS_TABLE,
				null,
				cv
		);

		return new Coop(newId, "New Coop", null, null, false, new ArrayList<>());
	}

	public void saveEvent(Event event) {
		long t = event.time.getTimeInMillis() / 1000L;

		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.EVENT_COOP, event.coop);
		cv.put(DatabaseHelper.EVENT_GROUP, event.group);
		cv.put(DatabaseHelper.EVENT_TIME, t);
		cv.put(DatabaseHelper.EVENT_COUNT, event.count);
		cv.put(DatabaseHelper.EVENT_DIR, event.direction);
		cv.put(DatabaseHelper.EVENT_PERSON, event.person);
		cv.put(DatabaseHelper.EVENT_NOTE_ID, event.notification);

		database.update(
				DatabaseHelper.EVENTS_TABLE,
				cv,
				DatabaseHelper._ID + " = " + event.id,
				null
		);
	}

	public void saveCoop(Coop coop) {
		long s = coop.startTime == null ? 0 : coop.startTime.getTimeInMillis() / 1000L;
		long e = coop.endTime == null ? 0 : coop.endTime.getTimeInMillis() / 1000L;

		Log.i(TAG, "Got message to update coop " + coop.name + " Start " + s + coop.startTime);

		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COOP_NAME, coop.name);
		cv.put(DatabaseHelper.START_TIME, s);
		cv.put(DatabaseHelper.END_TIME, e);
		cv.put(DatabaseHelper.COOP_SINK_MODE, coop.sinkMode);

		database.update(
				DatabaseHelper.COOPS_TABLE,
				cv,
				DatabaseHelper._ID + " = " + coop.id,
				null
		);

		for (Event ev : coop.events) {
			if (!ev.modified) continue;

			saveEvent(ev);
		}
	}

	public Coop fetchCoop(long _id) {
		final String[] coopCols = new String[]{
				DatabaseHelper._ID,
				DatabaseHelper.COOP_NAME,
				DatabaseHelper.START_TIME,
				DatabaseHelper.END_TIME,
				DatabaseHelper.COOP_SINK_MODE
		};
		final String[] eventCols = new String[]{
				DatabaseHelper._ID,
				DatabaseHelper.EVENT_COOP,
				DatabaseHelper.EVENT_GROUP,
				DatabaseHelper.EVENT_TIME,
				DatabaseHelper.EVENT_COUNT,
				DatabaseHelper.EVENT_PERSON,
				DatabaseHelper.EVENT_DIR,
				DatabaseHelper.EVENT_NOTE_ID
		};

		Cursor coop = database.query(
				DatabaseHelper.COOPS_TABLE,
				coopCols,
				DatabaseHelper._ID + " = " + _id,
				null, null, null, null);
		if (coop == null || coop.getCount() < 1) {
			return null;
		}
		coop.moveToFirst();

		ArrayList<Event> evs = new ArrayList<>();

		Calendar start;
		if (coop.getLong(2) == 0)
			start = null;
		else {
			start = Calendar.getInstance();
			start.setTimeInMillis(coop.getLong(2) * 1000L);
		}

		Calendar end;
		if (coop.getLong(3) == 0)
			end = null;
		else {
			end = Calendar.getInstance();
			end.setTimeInMillis(coop.getLong(3) * 1000L);
		}

		// If start time is unset, get everything from the past 2 days.
		long effectiveStart;
		if (coop.getLong(2) == 0) {
			// Get beginning of yesterday
			Calendar altStart = Calendar.getInstance();
			altStart.add(Calendar.DAY_OF_MONTH, -1);
			altStart.set(Calendar.HOUR_OF_DAY, 0);
			altStart.set(Calendar.MINUTE, 0);
			altStart.set(Calendar.SECOND, 0);
			altStart.set(Calendar.MILLISECOND, 0);

			effectiveStart = altStart.getTimeInMillis() / 1000L;
		} else {
			effectiveStart = coop.getLong(2);
		}

		long effectiveEnd;
		if (coop.getLong(3) == 0) {
			// Get 72 hours past the start time
			effectiveEnd = effectiveStart + 60 * 60 * 72;
		} else {
			effectiveEnd = coop.getLong(3);
		}

		Cursor events = database.query(
				DatabaseHelper.EVENTS_TABLE,
				eventCols,
				DatabaseHelper.EVENT_COOP + " = '" + coop.getString(1) + "' AND " +
						DatabaseHelper.EVENT_TIME + " BETWEEN " + effectiveStart + " AND " + effectiveEnd,
				null, null, null, null);
		if (events != null && events.moveToFirst()) {
			
			// TODO: A bandaid fix for duplicated events!
			long prevTime = 0;
			String prevPlayer = "";
			
			do {
				Calendar t = Calendar.getInstance();
				t.setTimeInMillis(events.getLong(3) * 1000L);
				
				if (t.getTimeInMillis() == prevTime && prevPlayer.equals(events.getString(5))) {
					continue;
				} else {
					prevTime = t.getTimeInMillis();
					prevPlayer = events.getString(5);
				}
				
				if (start == null || t.before(start)) {
					start = t;
				}

				evs.add(new Event(
						events.getLong(0),
						events.getString(1),
						events.getString(2),
						t,
						events.getInt(4),
						events.getString(5),
						events.getString(6),
						events.getInt(7)
				));
			} while (events.moveToNext());

			events.close();
		}

		Coop newCoop = new Coop(
				coop.getLong(0),
				coop.getString(1),
				start,
				end,
				coop.getInt(4) == 1,
				evs
		);
		coop.close();

		return newCoop;
	}

	public Cursor fetchCoops() {
		String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.COOP_NAME};
		Cursor cursor = database.query(
				DatabaseHelper.COOPS_TABLE,
				columns,
				null, null, null, null, DatabaseHelper._ID + " DESC");
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Coop fetchSelectedCoop() {
		SharedPreferences sharedPref = context.getSharedPreferences(
				MainActivity.PREFERENCES,
				Context.MODE_PRIVATE
		);
		long selectedCoop = sharedPref.getLong("SelectedCoop", -1);

		if (selectedCoop == -1) {
			return null;
		}

		return fetchCoop(selectedCoop);
	}

	public Coop fetchCoopByName(String name) {
		Cursor coop = database.query(
				DatabaseHelper.COOPS_TABLE,
				new String[]{DatabaseHelper._ID},
				DatabaseHelper.COOP_NAME + " = '" + name + "'",
				null,
				null,
				null,
				DatabaseHelper._ID + " DESC",
				"1"
		);
		if (coop == null || coop.getCount() < 1 || coop.getColumnCount() < 1) return null;
		coop.moveToFirst();
		long id = coop.getLong(0);
		coop.close();
		return fetchCoop(id);
	}

	public void deleteCoop(long _id) {
		database.delete(DatabaseHelper.COOPS_TABLE, DatabaseHelper._ID + " = " + _id, null);

		// TODO: Clean up left-over Events.
	}
}