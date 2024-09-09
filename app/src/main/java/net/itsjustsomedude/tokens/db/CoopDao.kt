package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface CoopDao {
    @Insert
    suspend fun insert(coop: Coop): Long

    @Update
    suspend fun update(coop: Coop)

    @Upsert
    suspend fun upsert(coop: Coop)

    @Delete
    suspend fun delete(coop: Coop)

    @Query("SELECT * FROM Coop ORDER BY id DESC")
    fun listCoops(): LiveData<List<Coop>>

    @Query("SELECT * FROM Coop WHERE id = :id")
    fun getCoop(id: Long): LiveData<Coop?>

    @Query("SELECT * FROM Coop WHERE name = :name AND contract = :kevId")
    fun getCoopByName(name: String, kevId: String): LiveData<Coop?>
}