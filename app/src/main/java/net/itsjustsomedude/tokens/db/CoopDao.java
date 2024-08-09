package net.itsjustsomedude.tokens.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface CoopDao {
	@Insert
	void insert(Coop coop);

	@Update
	void update(Coop coop);

	@Upsert
	void upsert(Coop coop);

	@Delete
	void delete(Coop coop);

	@Query("DELETE FROM Coop")
	void deleteAllCoops();

	@Query("SELECT * FROM Coop ORDER BY id ASC")
	List<Coop> getAllCoops();
}