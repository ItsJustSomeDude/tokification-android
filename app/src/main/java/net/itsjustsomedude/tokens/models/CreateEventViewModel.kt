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

class CreateEventViewModel(
    private val coopRepo: CoopRepository,
    private val eventRepo: EventRepository
) : ViewModel() {

    val selectedEvent = MutableLiveData<Event?>(null)
    val selectedCoop = MutableLiveData<Coop?>(null)

    fun createEvent(coopId: Long) {
        viewModelScope.launch {
            coopRepo.getCoopDirect(coopId)?.let { coop ->
                selectedCoop.value = coop

                selectedEvent.value = Event(
                    coop = coop.name,
                    kevId = coop.contract,
                    count = if (coop.sinkMode) 6 else 2,
                    direction = Event.DIRECTION_RECEIVED,
                    time = Calendar.getInstance(),
                    person = ""
                )
            } ?: run {
                selectedEvent.value = null
            }
        }
    }

    fun updateSelectedEvent(event: Event) {
        selectedEvent.value = event
    }

    suspend fun saveSelectedEvent() {
        selectedEvent.value?.let {
            viewModelScope.launch {
                eventRepo.upsert(it)
            }
        }
    }

    suspend fun saveSelectedEventCor() {
        selectedEvent.value?.let {
            eventRepo.upsertCor(it)
        }
    }

    //    val selectedEvent = MutableLiveData<Event?>(null)
//
//    fun loadEvent(id: Long?) {
//        if (id == null) {
//            selectedEvent.value = null
//            return
//        }
//
//        viewModelScope.launch {
//            val newEvent = eventRepo.getEventDirect(id)
//            println("Loaded event: $newEvent")
//
//            selectedEvent.value = newEvent
//        }
//    }
//
//    fun createEvent(count: Int = 1) {
//    }
}