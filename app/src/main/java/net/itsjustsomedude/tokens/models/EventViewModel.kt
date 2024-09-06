package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository

class EventViewModel(
    application: Application,
    eventId: Long,
    private val eventRepo: EventRepository = EventRepository(application),
    private val coopRepo: CoopRepository = CoopRepository(application)
) : AndroidViewModel(application) {
    
    val event: LiveData<Event?> = liveData {
        val newEvent = eventRepo.getEvent(eventId)
        emitSource(newEvent)
    }

    val coop = event.switchMap { ev ->
        liveData {
            emitSource(coopRepo.getCoopByName(ev?.coop ?: "", ev?.kevId ?: ""))
        }
    }

    fun insert(event: Event) {
        viewModelScope.launch {
            eventRepo.insert(event)
        }
    }

    fun update(event: Event) {
        viewModelScope.launch {
            eventRepo.update(event)
        }

    }

    fun delete(event: Event) {
        viewModelScope.launch {
            eventRepo.delete(event)
        }
    }
}