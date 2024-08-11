package net.itsjustsomedude.tokens.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface EventDao {
	@Insert
	void insert(Event event);

	@Update
	void update(Event event);

	@Upsert
	void upsert(Event event);

	@Delete
	void delete(Event event);

	@Query("SELECT * FROM Event WHERE coop = :coopId ORDER BY id ASC")
	LiveData<List<Event>> listEvents(int coopId);

	@Query("SELECT * FROM Event WHERE id = :id LIMIT 1")
	LiveData<Event> getEvent(int id);
}
