package net.itsjustsomedude.tokens;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	
	public static final String COOPS_TABLE = "Coops";
	public static final String EVENTS_TABLE = "Events";
	
	public static final String _ID = "_id";
	public static final String COOP_NAME = "CoopName";
	public static final String START_TIME = "StartTime";
	public static final String END_TIME = "EndTime";
	
	public static final String EVENT_COOP = "Coop";
	public static final String EVENT_TIME = "Time";
	public static final String EVENT_COUNT = "Count";
	public static final String EVENT_PERSON = "Person";
	public static final String EVENT_DIR = "Direction";
	public static final String EVENT_NOTE_ID = "NoteID";
	
	static final String DB_NAME = "Coops.db";
	
	static final int VERSION = 7;
	
	private static final String CREATE_COOPS_TABLE = "CREATE TABLE " +
	    COOPS_TABLE + "(" +
	    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    COOP_NAME + " TEXT NOT NULL, " +
	    START_TIME + " TEXT, " +
	    END_TIME + " TEXT);";
	
	private static final String CREATE_EVENTS_TABLE = "CREATE TABLE " +
	    EVENTS_TABLE + "(" +
	    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    EVENT_COOP + " INTEGER NOT NULL, " +
	    EVENT_TIME + " TEXT, " +
	    EVENT_COUNT + " INTEGER, " +
	    EVENT_PERSON + " TEXT, " +
	    EVENT_DIR + " TEXT, " +
	    EVENT_NOTE_ID + " INTEGER);";
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating DB");
		db.execSQL(CREATE_COOPS_TABLE);
		db.execSQL(CREATE_EVENTS_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		Log.i(TAG, "Upgrading DB.");
		//TODO: make a real migration.
		db.execSQL("DROP TABLE IF EXISTS " + COOPS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
		onCreate(db);
	}
}
