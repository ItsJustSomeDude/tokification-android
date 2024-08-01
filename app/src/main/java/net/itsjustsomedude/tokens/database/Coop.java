package net.itsjustsomedude.tokens.database;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
public class Coop {
	@PrimaryKey(autoGenerate = true)
	public long id;

    public String name;
	public String contract;
	public Calendar startTime;
	public Calendar endTime;
	public boolean sinkMode;
}
