package net.itsjustsomedude.tokens;
import android.content.Context;
import android.content.SharedPreferences;
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
	
	public Coop save(Context ctx) {
		Database db = new Database(ctx);
		db.open();
		db.saveCoop(this);
		
		Log.i(TAG, "Saved coop!");
		
		return this;
	}
	
	public void delete(Context ctx) {
		// Not saved, so don't try to delete.
		if (this.id == 0) return;
		
		Database db = new Database(ctx);
		db.open();
		db.deleteCoop(this.id);
		db.close();
	}
	
	public String[] getPeople(String sinkName) {
		ArrayList<String> out = new ArrayList<String>();
		
		for (Event ev : this.events) {
			if (!out.contains(ev.person)) out.add(ev.person);
		}
		
		if (sinkName != null) out.add(sinkName);
		
		return out.toArray(new String[0]);
	}
	
	public static void setSelectedCoop(Context ctx, long id) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(
			MainActivity.PREFERENCES,
			Context.MODE_PRIVATE
		);
		SharedPreferences.Editor edit = sharedPref.edit();
		edit.putLong("SelectedCoop", id);
		edit.apply();
	}
	
	public static Coop fetchSelectedCoop(Context ctx) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(
			MainActivity.PREFERENCES,
			Context.MODE_PRIVATE
		);
		long selectedCoop = sharedPref.getLong("SelectedCoop", -1);
		
		if (selectedCoop == -1) {
			return null;
		}
		
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
		return new Coop(0, "New Coop", null, null, new ArrayList<Event>());
	}
}
