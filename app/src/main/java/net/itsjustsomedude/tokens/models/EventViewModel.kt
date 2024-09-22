package net.itsjustsomedude.tokens.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository

class EventViewModel(
    eventId: Long,
    private val eventRepo: EventRepository,
    private val coopRepo: CoopRepository
) : ViewModel() {

    val event: LiveData<Event?> = liveData {
        val newEvent = eventRepo.getEvent(eventId)
        emitSource(newEvent)
    }

    val coop = event.switchMap { ev ->
        liveData {
            emitSource(coopRepo.getCoopByName(ev?.coop ?: "", ev?.kevId ?: ""))
        }
    }

    fun upsert() {
        viewModelScope.launch {
            event.value?.let { eventRepo.upsert(it) }
        }
    }

    fun delete(event: Event) {
        viewModelScope.launch {
            eventRepo.delete(event)
        }
    }
}