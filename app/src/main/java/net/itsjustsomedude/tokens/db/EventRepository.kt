package net.itsjustsomedude.tokens.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class EventRepository(application: Application) {
    private val eventDao: EventDao

    init {
        val database = AppDatabase.getInstance(application)
        eventDao = database.eventDao()
    }

    suspend fun getEvent(id: Long): LiveData<Event?> {
        return withContext(Dispatchers.IO) {
            eventDao.getEvent(id)
        }
    }

    fun getEventSync(id: Long): LiveData<Event?> {
        return eventDao.getEvent(id)
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
    fun blockingGetEvent(eventId: Long): LiveData<Event?> = runBlocking { getEvent(eventId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingListEvents(coop: String, kevId: String): LiveData<List<Event>> =
        runBlocking { listEvents(coop, kevId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingInsert(event: Event) = runBlocking { insert(event) }

    @Deprecated("Don't use blocking calls.")
    fun blockingUpdate(event: Event) = runBlocking { update(event) }

    @Deprecated("Don't use blocking calls.")
    fun blockingDelete(event: Event) = runBlocking { delete(event) }

    @Deprecated("Don't use blocking calls.")
    fun blockingDeleteAll(coop: String, kevId: String) = runBlocking { deleteAll(coop, kevId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingExists(coop: String, kevId: String, noteId: Int) =
        runBlocking { exists(coop, kevId, noteId) }


}