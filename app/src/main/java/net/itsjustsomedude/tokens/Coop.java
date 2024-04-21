package net.itsjustsomedude.tokens;
import android.content.Context;
import android.database.Cursor;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;

public class Coop {
    public long id;
	public String name;
	public Date startTime;
	public Date endTime;
	public ArrayList<Event> events;
	
	public boolean modified = false;
	
	public Coop(long i, String n, Date s, Date e, ArrayList<Event> ev) {
		this.id = i;
		this.name = n;
		this.startTime = s;
		this.endTime = e;
		this.events = ev;
	}
	
	public static Coop fetchSelectedCoop(Context ctx) {
		//TODO: get this from Shared Prefs.
		Long selectedCoop = 1l;
		
		Database db = new Database(ctx);
		db.open();
		return db.fetchCoop(selectedCoop);
	}
}
