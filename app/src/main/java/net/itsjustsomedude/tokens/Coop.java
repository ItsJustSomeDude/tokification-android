package net.itsjustsomedude.tokens;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;

public class Coop {
	private static final String TAG = "Coop";

	public long id;
	public String name;
	public String contract;
	public Calendar startTime;
	public Calendar endTime;
	public ArrayList<Event> events;
	public boolean sinkMode;

	public Coop(long i, String n, String c, Calendar s, Calendar e, boolean sinkMode, ArrayList<Event> ev) {
		this.id = i;
		this.name = n;
		this.contract = c;
		this.startTime = s;
		this.endTime = e;
		this.sinkMode = sinkMode;
		this.events = ev;
	}

	public void addEvent(Event toAdd) {
		this.events.add(toAdd);
	}

	public String[] getPeople(String sinkName) {
		ArrayList<String> out = new ArrayList<>();

		for (Event ev : this.events) {
			if (!out.contains(ev.person)) out.add(ev.person);
		}

		if (sinkName != null) out.add(sinkName);

		// TODO: Sort this list by the order of the first Received token.

		return out.toArray(new String[0]);
	}

	public static void setSelectedCoop(Context ctx, long id) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(
				MainActivity.PREFERENCES,
				Context.MODE_PRIVATE
		);
		sharedPref.edit().putLong("SelectedCoop", id).apply();
	}

	public static long getSelectedCoop(Context ctx) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(
				MainActivity.PREFERENCES,
				Context.MODE_PRIVATE
		);
		return sharedPref.getLong("SelectedCoop", 0);
	}
}
