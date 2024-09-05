package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface EventDao {
    @Insert
    fun insert(event: Event)

    @Update
    fun update(event: Event)

    @Upsert
    fun upsert(event: Event)

    @Delete
    fun delete(event: Event)

    @Query("SELECT EXISTS(SELECT * FROM Event WHERE coop = :coop AND kevId = :kevId AND notification = :noteId)")
    fun eventExists(coop: String, kevId: String, noteId: Int): Boolean

    @Query("DELETE FROM Event WHERE coop = :coop AND kevId = :kevId")
    fun deleteAll(coop: String, kevId: String)

    @Query("SELECT * FROM Event WHERE coop = :coop AND kevId = :kevId ORDER BY id ASC")
    fun listEvents(coop: String, kevId: String): LiveData<List<Event>>

    @Query("SELECT * FROM Event WHERE id = :id LIMIT 1")
    fun getEvent(id: Long): LiveData<Event?>
}
