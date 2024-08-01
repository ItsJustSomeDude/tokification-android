package net.itsjustsomedude.tokens.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Calendar;

@Entity
public class Event {
	@PrimaryKey(autoGenerate = true)
	public int id;

	public int coop; // this is the foreign key
	
	public Calendar time;
	public int count;
	public String group;
	public String person;
	public String direction;
	public int notification;

//		public Event(long _id, String coop, String group, Calendar time, int count, String person, String direction, int note) {
//			this.id = _id;
//			this.coop = coop;
//			this.group = group;
//			this.time = time;
//			this.count = count;
//			this.person = person;
//			this.direction = direction;
//			this.notification = note;
//		}
}

