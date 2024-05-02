package net.itsjustsomedude.tokens;
import java.util.Calendar;

public class Event {
	public long id;
	public Calendar time;
	public int count;
	public String person;
	public String direction;
	public int notification;
	
	public boolean modified = false;
	
	public Event(long _id, Calendar time, int count, String person, String direction, int note) {
		this.id = _id;
		this.time = time;
		this.count = count;
		this.person = person;
		this.direction = direction;
		this.notification = note;
	}
	
	public double tval(Calendar coopStart, Calendar coopEnd) {
		long tokenTime = this.time.getTimeInMillis() / 1000L;
		long startTime = coopStart.getTimeInMillis() / 1000L;
		long endTime = coopEnd.getTimeInMillis() / 1000L;
		
		double duration = endTime - startTime;
		double elapsed = tokenTime - startTime;
		
		double i = Math.pow(1 - 0.9 * (elapsed / duration), 4);
		double singleValue = Math.max(i, 0.03);
		
		return singleValue * this.count;
	}
}
