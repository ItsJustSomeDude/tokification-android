package net.itsjustsomedude.tokens.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import java.util.Calendar

class EventEditViewModel(
    // TODO: Find out if this is always present and needed.
    // I think the answer is "yes".
    coopId: Long,
    // If null, create event, otherwise edit.
    eventId: Long?,
    private val eventRepo: EventRepository,
    private val coopRepo: CoopRepository
) : ViewModel() {

    val event = MutableLiveData<Event>(null)
    val coop = MutableLiveData<Coop>(null)

    init {
        viewModelScope.launch {
            coopRepo.getCoopDirect(coopId)?.let {
                coop.value = it

                if (eventId == null)
                // Create Event
                    event.value = Event(
                        coop = it.name,
                        kevId = it.contract,
                        count = if (it.sinkMode) 6 else 2,
                        direction = Event.DIRECTION_RECEIVED,
                        time = Calendar.getInstance(),

                        // TODO: Boost Order - Set this to next player in boost order.
                        person = if (it.sinkMode) "Sink" else ""
                    )
                else {
                    // Fetch and Edit Event.
                    event.value = eventRepo.getEventDirect(eventId)
                    // TODO: Handle possible null events?
                }
            }
        }
    }

    fun updateEvent(newEvent: Event) {
        event.value = newEvent
    }

    fun updateEvent(
        coop: String? = null,
        kevId: String? = null,

        time: Calendar? = null,
        count: Int? = null,
        person: String? = null,
        direction: Int? = null,
        notification: Int? = null
    ) {
        event.value?.let { currentEvent ->
            val updatedEvent = currentEvent.copy(
                coop = coop ?: currentEvent.coop,
                kevId = kevId ?: currentEvent.kevId,
                time = time ?: currentEvent.time,
                count = count ?: currentEvent.count,
                person = person ?: currentEvent.person,
                direction = direction ?: currentEvent.direction,
                notification = notification ?: currentEvent.notification,
            )
            event.value = updatedEvent
        }
    }

    fun save() {
        event.value?.let {
            viewModelScope.launch {
                eventRepo.upsert(it)
            }
        }
    }

    suspend fun commit() {
        event.value?.let {
            eventRepo.upsertCor(it)
        }

    }
}