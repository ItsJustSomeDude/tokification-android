package net.itsjustsomedude.tokens;
import java.util.Date;

public class Event {
	public long id;
	public Date time;
	public int count;
	public String person;
	public String direction;
	public int notification;
	
	public boolean modified = false;
	
	public Event(long _id, Date time, int count, String person, String direction, int note) {
		
	}
}
