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
import java.util.Date;

public class Database {
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
	
	public void savrCoop(Coop coop) {
		String s = df.format(coop.startTime);
		String e = df.format(coop.endTime);
		
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper._ID, coop.id);
		cv.put(DatabaseHelper.COOP_NAME, coop.name);
		cv.put(DatabaseHelper.START_TIME, s);
		cv.put(DatabaseHelper.END_TIME, e);
		
		if (coop.id == 0) {
			// Coop has no id, so insert new.
		    database.insert(
			    DatabaseHelper.COOPS_TABLE,
			    null,
			    cv
		    );
		} else if (coop.modified) {
			// Existing record, changed coop, update.
			database.update(
				DatabaseHelper.EVENTS_TABLE,
				cv,
				DatabaseHelper._ID + " = " + coop.id,
				null
			);
		}
		// Exisiting unmodified coop
		
		for (Event ev : coop.events) {
			String t = df.format(ev.time);
			
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
		
		Cursor events = database.query(
			DatabaseHelper.EVENTS_TABLE,
			eventCols,
			DatabaseHelper.EVENT_COOP + " = " + _id,
			null, null, null, null);
		if (events != null && events.moveToFirst()) {
			do {
				Date t = null;
				try {
					t = df.parse(events.getString(1));
				} catch(ParseException err) {
					Log.e("DB", "Invalid date on event!");
					Log.e("DB", events.getString(1));
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
		
		Date start = null;
		try {
			start = df.parse(coop.getString(2));
		} catch(ParseException err) {}
		
		Date end = null;
		try {
			start = df.parse(coop.getString(3));
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
			null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public void deleteCoop(long _id) {
		database.delete(DatabaseHelper.DB_NAME, DatabaseHelper._ID + " = " + _id, null);
	}
}