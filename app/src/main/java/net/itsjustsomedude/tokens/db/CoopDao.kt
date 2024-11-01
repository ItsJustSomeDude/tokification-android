package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CoopDao {
    @Insert
    suspend fun insert(coop: Coop): Long

    @Update
    suspend fun update(coop: Coop)

    @Delete
    suspend fun delete(coop: Coop)

    @Query("DELETE FROM Coop WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM Coop ORDER BY id DESC")
    fun listCoopsLiveData(): LiveData<List<Coop>>

    @Query("SELECT * FROM Coop WHERE id = :id")
    fun getCoopLiveData(id: Long): LiveData<Coop?>

    @Query("SELECT * FROM Coop WHERE name = :name AND contract = :kevId")
    fun getCoopLiveDataByName(name: String, kevId: String): LiveData<Coop?>

    @Query("SELECT * FROM Coop WHERE name = :name AND contract = :kevId")
    suspend fun getCoopByName(name: String, kevId: String): Coop?

    @Query("SELECT * FROM Coop WHERE id = :id")
    suspend fun getCoop(id: Long): Coop?
}