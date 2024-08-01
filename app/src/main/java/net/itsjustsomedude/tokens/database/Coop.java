package net.itsjustsomedude.tokens.database;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity
public class Coop {
	@PrimaryKey(autoGenerate = true)
	public long id;

	@ColumnInfo
	public String firstName;

	@ColumnInfo
	public String lastName;

	@Relation(
			parentColumn = "id",
			entityColumn = "coop"
	)
	public List<Event> events;
}
