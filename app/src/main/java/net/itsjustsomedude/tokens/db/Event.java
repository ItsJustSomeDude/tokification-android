package net.itsjustsomedude.tokens.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity
public class Event {
    public static final int DIRECTION_SENT = 1;
    public static final int DIRECTION_RECEIVED = 2;

    @PrimaryKey(autoGenerate = true)
    public Long id;

    public Calendar time;
    public int count;
    public String coop;
    public String kevId;
    public String person;
    public int direction;
    public int notification;

    public Event(String coop, String kevId, Calendar time, int count, String person, int direction, int note) {
        this.id = 0L;
        this.coop = coop;
        this.kevId = kevId;
        this.time = time;
        this.count = count;
        this.person = person;
        this.direction = direction;
        this.notification = note;
    }

    public Event(String coop, String kevId, Calendar time, int count, String person, int direction) {
        this(coop, kevId, time, count, person, direction, 0);
    }
}

