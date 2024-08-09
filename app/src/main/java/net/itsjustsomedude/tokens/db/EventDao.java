package net.itsjustsomedude.tokens.db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

public interface EventDao {
	@Insert
	void insert(Event event);

	@Update
	void update(Event event);

	@Upsert
	void upsert(Event event);

	@Delete
	void delete(Event event);

	@Query("SELECT * FROM Event ORDER BY id ASC")
	List<Event> getAll();
}
