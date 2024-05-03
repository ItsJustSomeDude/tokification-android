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

	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	public Database(Context c) {
		context = c;
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveCoop(Coop coop) {
		String s = "";
		String e = "";
		if (coop.startTime != null) s = df.format(coop.startTime.getTime());
		if (coop.endTime != null) e = df.format(coop.endTime.getTime());

		Log.i(TAG, "Got message to update coop " + coop.name + " Start " + s + coop.startTime);

		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COOP_NAME, coop.name);
		cv.put(DatabaseHelper.START_TIME, s);
		cv.put(DatabaseHelper.END_TIME, e);

		long newId = -1;
		if (coop.id == 0) {
			// Coop has no id, so insert new.
			newId = database.insert(
					DatabaseHelper.COOPS_TABLE,
					null,
					cv
			);
		} else /* if (coop.modified) */ {
			// Existing record, changed coop, update.
			database.update(
					DatabaseHelper.COOPS_TABLE,
					cv,
					DatabaseHelper._ID + " = " + coop.id,
					null
			);
			Log.i(TAG, "Updated coop " + coop.name);
		}
		// Existing unmodified coop

		// If this is -1, means existing coop.
		// This is used to store the coop ID in the Events.
		if (newId != -1)
			coop.id = newId;

		for (Event ev : coop.events) {
			String t = df.format(ev.time.getTime());

			ContentValues ecv = new ContentValues();
			ecv.put(DatabaseHelper.EVENT_COOP, coop.id);
			ecv.put(DatabaseHelper.EVENT_TIME, t);
			ecv.put(DatabaseHelper.EVENT_COUNT, ev.count);
			ecv.put(DatabaseHelper.EVENT_DIR, ev.direction);
			ecv.put(DatabaseHelper.EVENT_PERSON, ev.person);
			ecv.put(DatabaseHelper.EVENT_NOTE_ID, ev.notification);

			if (ev.id == 0) {
				// New event
				database.insert(
						DatabaseHelper.EVENTS_TABLE,
						null,
						ecv
				);
			} else if (ev.modified) {
				database.update(
						DatabaseHelper.EVENTS_TABLE,
						ecv,
						DatabaseHelper._ID + " = " + ev.id,
						null
				);
			}
			// Existing Event, not modified.
		}
	}

	public Coop fetchCoop(long _id) {
		final String[] coopCols = new String[]{
				DatabaseHelper._ID,
				DatabaseHelper.COOP_NAME,
				DatabaseHelper.START_TIME,
				DatabaseHelper.END_TIME
		};
		final String[] eventCols = new String[]{
				DatabaseHelper._ID,
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

		Log.i(TAG, "Finding events where " + DatabaseHelper.EVENT_COOP + " is " + coop.getLong(0));

		Cursor events = database.query(
				DatabaseHelper.EVENTS_TABLE,
				eventCols,
				// DatabaseHelper.EVENT_COOP + " = '" + coop.getString(1) + "'",
				DatabaseHelper.EVENT_COOP + " = " + coop.getLong(0),
				null, null, null, null);
		if (events != null && events.moveToFirst()) {
			do {
				Calendar t = null;
				try {
					Date parsed = df.parse(events.getString(1));
					if (parsed != null) {
						t = Calendar.getInstance();
						t.setTime(parsed);
					}
				} catch (ParseException err) {
					Log.e("DB", "Invalid date on event!");
					Log.e("DB", events.getString(1), err);
					continue;
				}

				evs.add(new Event(
						events.getLong(0),
						t,
						events.getInt(2),
						events.getString(3),
						events.getString(4),
						events.getInt(5)
				));
			} while (events.moveToNext());

			events.close();
		}

		Calendar start = null;
		try {
			Date parsed = df.parse(coop.getString(2));
			if (parsed != null) {
				start = Calendar.getInstance();
				start.setTime(parsed);
			}
		} catch (ParseException ignored) {
		}

		Calendar end = null;
		try {
			Date parsed = df.parse(coop.getString(3));
			if (parsed != null) {
				end = Calendar.getInstance();
				end.setTime(parsed);
			}
		} catch (ParseException ignored) {
		}

		Coop newCoop = new Coop(
				coop.getLong(0),
				coop.getString(1),
				start,
				end,
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