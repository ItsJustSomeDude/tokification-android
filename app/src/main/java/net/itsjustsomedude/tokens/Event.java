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
		this.id = _id;
		this.time = time;
		this.count = count;
		this.person = person;
		this.direction = direction;
		this.notification = note;
	}
	
	public double tval(Date coopStart, Date coopEnd) {
		long tokenTime = this.time.getTime();
		long startTime = coopStart.getTime();
		long endTime = coopEnd.getTime();
		
		long duration = endTime - startTime;
		long elapsed = tokenTime - startTime;
		
		double i = Math.pow(1 - 0.9 * (elapsed / duration), 4);
		double singleValue = Math.max(i, 0.03);
		
		return singleValue * this.count;
	}
}
