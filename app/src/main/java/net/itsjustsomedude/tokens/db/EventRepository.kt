package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

class EventRepository(private val eventDao: EventDao) {

    suspend fun getEvent(id: Long): LiveData<Event?> {
        return withContext(Dispatchers.IO) {
            eventDao.getEvent(id)
        }
    }

    suspend fun getEventDirect(id: Long): Event? {
        return withContext(Dispatchers.IO) {
            eventDao.getEventDirect(id)
        }
    }

    suspend fun listEventsDirect(coop: String, kevId: String): List<Event> {
        return withContext(Dispatchers.IO) {
            eventDao.listEventsDirect(coop, kevId)
        }
    }

    suspend fun listEvents(coop: String, kevId: String): LiveData<List<Event>> {
        return withContext(Dispatchers.IO) {
            eventDao.listEvents(coop, kevId)
        }
    }

    suspend fun exists(coop: String, kevId: String, noteId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            eventDao.eventExists(coop, kevId, noteId)
        }
    }

    suspend fun create(
        coop: String,
        kevId: String,
        time: Calendar = Calendar.getInstance(),
        count: Int = 1,
        player: String = "",
        direction: Int = Event.DIRECTION_RECEIVED
    ): Long {
        return withContext(Dispatchers.IO) {
            eventDao.insert(
                Event(
                    coop = coop,
                    kevId = kevId,
                    time = time,
                    count = count,
                    person = player,
                    direction = direction
                )
            )
        }
    }

    fun insert(event: Event) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.insert(event)
        }
    }

    fun update(event: Event) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.update(event)
        }
    }

    fun upsert(event: Event) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.upsert(event)
        }
    }

    suspend fun upsertCor(event: Event) {
        eventDao.upsert(event)
    }

    fun deleteById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.deleteById(id)
        }
    }

    fun delete(event: Event) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.delete(event)
        }
    }

    fun deleteAll(coop: String, kevId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.deleteAll(coop, kevId)
        }
    }

    @Deprecated("Don't use blocking calls.")
    fun blockingInsert(event: Event) = runBlocking { insert(event) }

    @Deprecated("Don't use blocking calls.")
    fun blockingExists(coop: String, kevId: String, noteId: Int) =
        runBlocking { exists(coop, kevId, noteId) }
}