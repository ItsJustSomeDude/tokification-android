package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository

class EventViewModel(
    application: Application,
    eventId: Long,
    private val eventRepo: EventRepository = EventRepository(application),
    private val coopRepo: CoopRepository = CoopRepository(application)
) : AndroidViewModel(application) {
//    private val eventRepo = EventRepository(application)
//    private val coopRepo = CoopRepository(application)

//    private val _eventId = MutableLiveData<Long>(0)
//
//    val event: LiveData<Event?> = _eventId.switchMap { id ->
//        println("Fetching Event")
//        eventRepo.getEventSync(id)
//    }
//
//    val coop: LiveData<Coop?> = event.switchMap {
//        it?.let { ev ->
//            println("Fetching Coop")
//            coopRepo.getCoopByNameSync(ev.coop, ev.kevId)
//        }
//    }
//
//    fun setEvent(id: Long) {
//        _eventId.value = id
//    }

    private val _event = MediatorLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _coop = MediatorLiveData<Coop?>()
    val coop: LiveData<Coop?> = _coop

    init {
        viewModelScope.launch {
            val newEvent = eventRepo.getEvent(eventId)

            _event.addSource(newEvent) { value ->
                _event.value = value
            }

            _event.value?.let { ev ->
                val newCoop = coopRepo.getCoopByName(ev.coop, ev.kevId)

                _coop.addSource(newCoop) { value ->
                    _coop.value = value
                }
            }
        }
    }

//    lateinit var event: LiveData<Event?>
//    lateinit var coop: LiveData<Coop?>
//
//    init {
//        viewModelScope.launch {
//            val newEvent = eventRepo.getEvent(eventId)
//            event = newEvent
//
//            coop = coopRepo.getCoopByName(newEvent.value?.coop ?: "", newEvent.value?.coop ?: "")
//        }
//    }

//    private val _event = MutableLiveData<Event?>()
//    val event: LiveData<Event?> get() = _event
//
//    private val _coop = MutableLiveData<Coop?>()
//    val coop: LiveData<Coop?> get() = _coop
//
//    init {
//        viewModelScope.launch {
//            val newEvent = eventRepo.getEvent(eventId)
//            _event.value = newEvent.value
//            newEvent.value?.let {
//                _coop.value = coopRepo.getCoopByName(it.coop ?: "", it.coop ?: "").value
//            }
//        }
//    }

//    private val _event: MutableLiveData<Event?> = MutableLiveData()
//    val event: LiveData<Event?> get() = _event
//
//    private val _coop: MutableLiveData<Coop?> = MutableLiveData()
//    val coop: LiveData<Coop?> get() = _coop
//
//    init {
//        viewModelScope.launch {
//            val eventLiveData = eventRepo.getEvent(eventId)
//            eventLiveData.observeForever { ev ->
//                _event.value = ev
//
//
//            }
//        }
//    }

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