package net.itsjustsomedude.tokens.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.List;

//@Entity
//data class User(
//        @PrimaryKey val uid: Int,
//        @ColumnInfo(name = "first_name") val firstName: String?,
//        @ColumnInfo(name = "last_name") val lastName: String?
//)

@Entity
public class Coop {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String contract;
    public Calendar startTime;
    public Calendar endTime;
    public boolean sinkMode;

    public List<String> players;

    public Coop() {
        this("New Coop", "KevID", null, null, false);
    }

    public Coop(String name, String kevId, Calendar startTime, Calendar endTime, boolean sinkMode) {
        this.name = name;
        this.contract = kevId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sinkMode = sinkMode;
    }
}
