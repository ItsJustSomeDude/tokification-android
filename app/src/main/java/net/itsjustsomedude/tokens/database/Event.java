package net.itsjustsomedude.tokens.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Event {
	@PrimaryKey(autoGenerate = true)
	public int id;

	public int coop; // this is the foreign key

	public String description;
	// other fields
}
