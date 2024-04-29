package net.itsjustsomedude.tokens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Database {
	private static final String TAG = "Database";
	
	private DatabaseHelper dbHelper;
	private Context context;
	private SQLiteDatabase database;
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Database(Context c) {
		context = c;
	}
	
	public Database open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void saveCoop(Coop coop) {
		String s = "";
		String e = "";
        if (coop.startTime != null) s = df.format(coop.startTime.getTime());
		if (coop.endTime != null) e = df.format(coop.endTime.getTime());
		
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COOP_NAME, coop.name);
		cv.put(DatabaseHelper.START_TIME, s);
		cv.put(DatabaseHelper.END_TIME, e);
		
		long newId = -1;
		if (coop.id == 0) {
			// Coop has no id, so insert new.
		    database.insert(
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
		}
		// Exisiting unmodified coop
		
		// If this is -1, means existing coop.
		// This is used to store the coop ID in the Events.
		if (newId == -1)
		    newId = coop.id;
		
		for (Event ev : coop.events) {
			String t = df.format(ev.time.getTime());
			
			ContentValues ecv = new ContentValues();
			ecv.put(DatabaseHelper.EVENT_COOP, newId);
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
			// Exisiting Event, not modified.
		}
	}
	
	public Coop fetchCoop(long _id) {
		final String[] coopCols = new String[] {
			DatabaseHelper._ID,
			DatabaseHelper.COOP_NAME,
			DatabaseHelper.START_TIME,
			DatabaseHelper.END_TIME
		};
		final String[] eventCols = new String[] {
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
		if (coop == null) {
			return null;
		}
		coop.moveToFirst();
		
		ArrayList<Event> evs = new ArrayList<Event>();
		
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
					t = Calendar.getInstance();
					t.setTime(parsed);
				} catch(ParseException err) {
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
			} while(events.moveToNext());
		}
		
		Calendar start = null;
		try {
			Date parsed = df.parse(coop.getString(2));
			start = Calendar.getInstance();
			start.setTime(parsed);
		} catch(ParseException err) {}
		
		Calendar end = null;
		try {
			Date parsed = df.parse(coop.getString(3));
			end = Calendar.getInstance();
			end.setTime(parsed);
		} catch(ParseException err) {}
		
		return new Coop(
			coop.getLong(0),
			coop.getString(1),
			start,
			end,
			evs
		);
	}

    public Cursor fetchCoops() {
		String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.COOP_NAME };
		Cursor cursor = database.query(
			DatabaseHelper.COOPS_TABLE,
			columns,
			null, null, null, null, DatabaseHelper._ID + " DESC");
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
		
//		return database.rawQuery(
//			"SELECT * FROM " + DatabaseHelper.COOPS_TABLE + ";"
//			, null);
	}
	
	public void deleteCoop(long _id) {
		database.delete(DatabaseHelper.DB_NAME, DatabaseHelper._ID + " = " + _id, null);
	}
}