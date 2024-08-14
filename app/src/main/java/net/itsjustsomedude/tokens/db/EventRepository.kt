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

    suspend fun getEvent(id: Int): LiveData<Event>? {
        return withContext(Dispatchers.IO) {
            eventDao.getEvent(id)
        }
    }

    suspend fun listEvents(coopId: Int): LiveData<List<Event>> {
        return withContext(Dispatchers.IO) {
            eventDao.listEvents(coopId)
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

//    fun deleteAll() {
//        CoroutineScope(Dispatchers.IO).launch {
//            eventDao.delete()
//        }
//    }

    @Deprecated("Don't use blocking calls.")
    fun blockingGetEvent(eventId: Int): LiveData<Event>? = runBlocking { getEvent(eventId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingListEvents(coopId: Int): LiveData<List<Event>> = runBlocking { listEvents(coopId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingInsert(event: Event) = runBlocking { insert(event) }

    @Deprecated("Don't use blocking calls.")
    fun blockingUpdate(event: Event) = runBlocking { update(event) }

    @Deprecated("Don't use blocking calls.")
    fun blockingDelete(event: Event) = runBlocking { delete(event) }

//    @Deprecated("Don't use blocking calls.")
//    fun blockingDeleteAll() = runBlocking { deleteAll() }

}