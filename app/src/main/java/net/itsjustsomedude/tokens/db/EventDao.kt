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
	suspend fun insert(event: Event): Long

	@Upsert
	suspend fun upsert(event: Event)

	@Update
	suspend fun update(event: Event)

	@Delete
	suspend fun delete(event: Event)

	@Query("DELETE FROM Event WHERE id = :id")
	suspend fun deleteById(id: Long)

	@Query("SELECT EXISTS(SELECT * FROM Event WHERE coop = :coop AND kevId = :kevId AND notification = :noteId)")
	suspend fun eventExists(coop: String, kevId: String, noteId: Int): Boolean

	@Query("DELETE FROM Event WHERE coop = :coop AND kevId = :kevId")
	suspend fun deleteAll(coop: String, kevId: String)

	@Query("SELECT * FROM Event WHERE coop = :coop AND kevId = :kevId ORDER BY time DESC")
	fun listEventsLiveData(coop: String, kevId: String): LiveData<List<Event>>

	@Query("SELECT * FROM Event WHERE id = :id LIMIT 1")
	fun getEventLiveData(id: Long): LiveData<Event?>

	@Query("SELECT * FROM Event WHERE id = :id LIMIT 1")
	suspend fun getEvent(id: Long): Event?

	@Query("SELECT * FROM Event WHERE coop = :coop AND kevId = :kevId ORDER BY time DESC")
	suspend fun listEvents(coop: String, kevId: String): List<Event>
}
