package net.itsjustsomedude.tokens;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;

public class Coop {
	private static final String TAG = "Coop";
	
    public long id;
	public String name;
	public Calendar startTime;
	public Calendar endTime;
	public ArrayList<Event> events;
	
	public boolean modified = false;
	
	public Coop(long i, String n, Calendar s, Calendar e, ArrayList<Event> ev) {
		this.id = i;
		this.name = n;
		this.startTime = s;
		this.endTime = e;
		this.events = ev;
	}
	
	public Event addEvent(Calendar time, int count, String direction, String person) {
		return this.addEvent(time, count, direction, person, 0);
	}
	
	public Event addEvent(Calendar time, int count, String direction, String person, int notification) {
		Event toAdd = new Event(0, time, count, person, direction, notification);
		this.events.add(toAdd);
		return toAdd;
	}
	
	public void save(Context ctx) {
		Database db = new Database(ctx);
		db.open();
		db.saveCoop(this);
		
		Log.i(TAG, "Saved coop!");
	}
	
	public String[] getPeople() {
		ArrayList<String> out = new ArrayList<String>();
		
		for (Event ev : this.events) {
			if (!out.contains(ev.person)) out.add(ev.person);
		}
		
		return out.toArray(new String[0]);
	}
	
	public static Coop fetchSelectedCoop(Context ctx) {
		//TODO: get this from Shared Prefs.
		long selectedCoop = 1l;
		
		Database db = new Database(ctx);
		db.open();
		Coop coop = db.fetchCoop(selectedCoop);
		db.close();
		return coop;
	}
	
	public static Cursor fetchCoops(Context ctx) {
		Database db = new Database(ctx);
		db.open();
		return db.fetchCoops();
		
		//TODO: close.
	}
	
	public static Coop createCoop() {
		return new Coop(0, "better858", null, null, new ArrayList<Event>());
	}
}
