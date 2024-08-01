package net.itsjustsomedude.tokens.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;

import net.itsjustsomedude.tokens.Coop;

@Dao
public abstract class CoopDao extends RoomDatabase {
	@Transaction
	@Query("SELECT * FROM Coop WHERE id = :coopId")
	LiveData<CoopWithEvents> getById(int coopId);

	@Query("SELECT * FROM coop WHERE first_name LIKE :coopName LIMIT 1")
	net.itsjustsomedude.tokens.Coop getByName(String coopName);

	@Insert
	void insertAll(net.itsjustsomedude.tokens.Coop... coops);

	@Delete
	void delete(Coop coop);

}
