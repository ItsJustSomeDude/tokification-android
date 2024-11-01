package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData
import java.util.Calendar

class EventRepository(private val eventDao: EventDao) {

    fun listEventsLiveData(coop: String, kevId: String): LiveData<List<Event>> =
        eventDao.listEventsLiveData(coop, kevId)

    suspend fun listEvents(coop: String, kevId: String): List<Event> =
        eventDao.listEvents(coop, kevId)

    fun getEventLiveData(id: Long): LiveData<Event?> =
        eventDao.getEventLiveData(id)

    suspend fun getEvent(id: Long): Event? =
        eventDao.getEvent(id)

    suspend fun exists(coop: String, kevId: String, noteId: Int): Boolean =
        eventDao.eventExists(coop, kevId, noteId)

    suspend fun insert(event: Event) =
        eventDao.insert(event)

    suspend fun update(event: Event) =
        eventDao.update(event)

    suspend fun upsert(event: Event) =
        eventDao.upsert(event)

    suspend fun delete(event: Event) =
        eventDao.delete(event)

    suspend fun deleteAll(coop: String, kevId: String) =
        eventDao.deleteAll(coop, kevId)

    fun newEvent(
        coop: Coop,
        time: Calendar = Calendar.getInstance(),
        count: Int = if (coop.sinkMode) 6 else 2,
        direction: Int = Event.DIRECTION_RECEIVED,
        // TODO: Boost Order Next Person
        person: String = if (coop.sinkMode) "" else "Sink",
    ) = Event(
        coop = coop.name,
        kevId = coop.contract,
        time = time,
        count = count,
        direction = direction,
        person = person
    )
}