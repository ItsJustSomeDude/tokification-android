package net.itsjustsomedude.tokens;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;

public class Coop {
	private static final String TAG = "Coop";

	public long id;
	public String name;
	public Calendar startTime;
	public Calendar endTime;
	public ArrayList<Event> events;

	public boolean sinkMode;

	public boolean modified = false;

	public Coop(long i, String n, Calendar s, Calendar e, boolean sinkMode, ArrayList<Event> ev) {
		this.id = i;
		this.name = n;
		this.startTime = s;
		this.endTime = e;
		this.sinkMode = sinkMode;
		this.events = ev;
	}

	public void addEvent(Event toAdd) {
		this.events.add(toAdd);
	}

//	public Event addEvent(Calendar time, int count, String direction, String person) {
//		return this.addEvent(time, count, direction, person, 0);
//	}

//	public Event addEvent(Calendar time, int count, String direction, String person, int notification) {
//		Event toAdd = new Event(0, time, count, person, direction, notification);
//		this.events.add(toAdd);
//		return toAdd;
//	}

	public String[] getPeople(String sinkName) {
		ArrayList<String> out = new ArrayList<>();

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
}
