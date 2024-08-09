package net.itsjustsomedude.tokens.db;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import net.itsjustsomedude.tokens.MainActivity;

import java.util.Calendar;

@Entity
public class Coop {
	@PrimaryKey(autoGenerate = true)
	public long id;

	public String name;
	public String contract;
	public Calendar startTime;
	public Calendar endTime;
	public boolean sinkMode;

//	public void addEvent(net.itsjustsomedude.tokens.database.Coop.Event toAdd) {
//		this.events.add(toAdd);
//	}

//	public String[] getPeople(String sinkName) {
//		ArrayList<String> out = new ArrayList<>();
//
//		for (net.itsjustsomedude.tokens.database.Coop.Event ev : this.events) {
//			if (!out.contains(ev.person)) out.add(ev.person);
//		}
//
//		if (sinkName != null) out.add(sinkName);
//
//		// TODO: Sort this list by the order of the first Received token.
//
//		return out.toArray(new String[0]);
//	}

	public static void setSelectedCoop(Context ctx, long id) {
		// TODO: Replace with AndroidX preference.
		SharedPreferences sharedPref = ctx.getSharedPreferences(
				MainActivity.PREFERENCES,
				Context.MODE_PRIVATE
		);
		sharedPref.edit().putLong("SelectedCoop", id).apply();
	}

	public static long getSelectedCoop(Context ctx) {
		// TODO: Replace with AndroidX preference.
		SharedPreferences sharedPref = ctx.getSharedPreferences(
				MainActivity.PREFERENCES,
				Context.MODE_PRIVATE
		);
		return sharedPref.getLong("SelectedCoop", 0);
	}

}
