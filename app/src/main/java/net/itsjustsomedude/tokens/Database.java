package net.itsjustsomedude.tokens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class Database {
	private static final String TAG = "Database";
	private final DatabaseHelper dbHelper;
	private final Context context;
	private final SQLiteDatabase database;

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

	public Coop.Event createEvent(String coop, String group, Calendar time, int count, String direction, String person) {
		return createEvent(coop, group, time, count, direction, person, 0);
	}

	public Coop.Event createEvent(String coop, String group, Calendar time, int count, String direction, String person, int notification) {
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

		return new Coop.Event(newId, coop, group, time, count, person, direction, notification);
	}

	public Coop createCoop() {
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COOP_NAME, "New Coop");
		cv.put(DatabaseHelper.COOP_GROUP, "KevID");
		cv.put(DatabaseHelper.START_TIME, 0);
		cv.put(DatabaseHelper.END_TIME, 0);
		cv.put(DatabaseHelper.COOP_SINK_MODE, false);

		long newId = database.insert(
				DatabaseHelper.COOPS_TABLE,
				null,
				cv
		);

		// TODO: Read default SinkMode from SP and set here.

		return new Coop(newId, "New Coop", "KevID", null, null, false, new ArrayList<>());
	}

	public void saveEvent(Coop.Event event) {
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
		cv.put(DatabaseHelper.COOP_GROUP, coop.contract);
		cv.put(DatabaseHelper.START_TIME, s);
		cv.put(DatabaseHelper.END_TIME, e);
		cv.put(DatabaseHelper.COOP_SINK_MODE, coop.sinkMode);

		database.update(
				DatabaseHelper.COOPS_TABLE,
				cv,
				DatabaseHelper._ID + " = " + coop.id,
				null
		);

//		for (Coop.Event ev : coop.events) {
//			if (!ev.modified) continue;
//
//			saveEvent(ev);
//		}
	}

	public Coop fetchCoop(long _id) {
		final String[] coopCols = new String[]{
				DatabaseHelper._ID,
				DatabaseHelper.COOP_NAME,
				DatabaseHelper.COOP_GROUP,
				DatabaseHelper.START_TIME,
				DatabaseHelper.END_TIME,
				DatabaseHelper.COOP_SINK_MODE
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

		Calendar start;
		if (coop.getLong(3) == 0)
			start = null;
		else {
			start = Calendar.getInstance();
			start.setTimeInMillis(coop.getLong(3) * 1000L);
		}

		Calendar end;
		if (coop.getLong(4) == 0)
			end = null;
		else {
			end = Calendar.getInstance();
			end.setTimeInMillis(coop.getLong(4) * 1000L);
		}

		ArrayList<Coop.Event> evs = fetchEventList(coop.getString(1), coop.getString(2));

		Coop.Event firstEvent = !evs.isEmpty() ? evs.get(0) : null;
		if (firstEvent != null && firstEvent.time.before(start))
			start = firstEvent.time;

		Coop newCoop = new Coop(
				coop.getLong(0),
				coop.getString(1),
				coop.getString(2),
				start,
				end,
				coop.getInt(5) == 1,
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

	public Cursor fetchEvents(String coop, String contract) {
		// TODO: Sort by date...
		Cursor events = database.query(
				DatabaseHelper.EVENTS_TABLE,
				eventCols,
				DatabaseHelper.EVENT_COOP + " = '" + coop + "' AND " +
						DatabaseHelper.EVENT_GROUP + " = '" + contract + "'",
				null, null, null, DatabaseHelper.EVENT_TIME + " DESC");
		if (events != null) events.moveToFirst();
		return events;
	}

	public ArrayList<Coop.Event> fetchEventList(String coop, String contract) {
		ArrayList<Coop.Event> out = new ArrayList<>();

		Cursor event = fetchEvents(coop, contract);

		if (event == null || event.getCount() < 1 || !event.moveToFirst()) {
			if (event != null) event.close();
			return out;
		}

		// TODO: A bandaid fix for duplicated events!
		long prevTime = 0;
		String prevPlayer = "";

		do {
			Calendar t = Calendar.getInstance();
			t.setTimeInMillis(event.getLong(3) * 1000L);

			// Moar bandaid!
			if (t.getTimeInMillis() == prevTime && prevPlayer.equals(event.getString(5))) {
				continue;
			} else {
				prevTime = t.getTimeInMillis();
				prevPlayer = event.getString(5);
			}

			out.add(new Coop.Event(
					event.getLong(0),
					event.getString(1),
					event.getString(2),
					t,
					event.getInt(4),
					event.getString(5),
					event.getString(6),
					event.getInt(7)
			));
		} while (event.moveToNext());

		event.close();

		return out;
	}

	public void deleteCoop(long _id, boolean deleteEvents) {
		Coop toDelete = fetchCoop(_id);

		if (toDelete == null) return;

		String coop = toDelete.name;
		String contract = toDelete.contract;

		if (deleteEvents)
			database.delete(
					DatabaseHelper.EVENTS_TABLE,
					DatabaseHelper.EVENT_COOP + " = '" + coop + "' AND " +
							DatabaseHelper.EVENT_GROUP + " = '" + contract + "'",
					null
			);

		database.delete(DatabaseHelper.COOPS_TABLE, DatabaseHelper._ID + " = " + _id, null);
	}

	public void deleteEvent(long _id) {
		database.delete(DatabaseHelper.EVENTS_TABLE, DatabaseHelper._ID + " = " + _id, null);
	}
}