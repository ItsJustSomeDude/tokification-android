package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.ClipboardHelper
import net.itsjustsomedude.tokens.NotificationHelper
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reports.SinkReport
import java.util.Calendar

class CoopViewModel(
    private val coopId: Long,
    private val coopRepo: CoopRepository,
    private val eventRepo: EventRepository,
    private val notificationHelper: NotificationHelper,
    private val clipboard: ClipboardHelper
) : ViewModel() {
    var showEventList = mutableStateOf(false)
    var showEventEdit = mutableStateOf(false)
    var showNameEdit = mutableStateOf(false)

    val coop: LiveData<Coop?> = liveData {
        println("Fetching Coop: $coopId")
        emitSource(coopRepo.getCoop(coopId))
    }

    val events = coop.switchMap { co ->
        liveData {
            if (co != null)
                emitSource(eventRepo.listEvents(co.name, co.contract))
            else
                emit(emptyList())
            // Return "void"
            Unit
        }
    }

    val selectedEvent = MutableLiveData<Event?>(null)

    fun loadEvent(id: Long?) {
        if (id == null) {
            selectedEvent.value = null
            return
        }

        viewModelScope.launch {
            val newEvent = eventRepo.getEventDirect(id)
            println("Loaded event: $newEvent")

            selectedEvent.value = newEvent
        }
    }

    fun createEvent(count: Int = 1) {
        coop.value?.let { coop ->
            selectedEvent.value = Event(
                coop = coop.name,
                kevId = coop.contract,
                count = count,
                direction = Event.DIRECTION_RECEIVED,
                time = Calendar.getInstance(),
                person = ""
            )
        } ?: run {
            selectedEvent.value = null
        }
    }

    fun updateSelectedEvent(event: Event) {
        selectedEvent.value = event
    }

    fun saveSelectedEvent() {
        selectedEvent.value?.let {
            viewModelScope.launch {
                eventRepo.upsert(it)
            }
        }
    }

    fun update(coop: Coop) {
        viewModelScope.launch {
            coopRepo.update(coop)
        }
    }

    fun delete(coop: Coop) {
        viewModelScope.launch {
            coopRepo.delete(coop)
        }
    }

//    fun

    fun copySinkReport() {
        coop.value?.let {
            val report = SinkReport().generate(coop = it, events = events.value ?: emptyList())

            clipboard.copyText(report)
        }
    }

    fun postActions() {
        coop.value?.let { c ->
            events.value?.let { e ->
                notificationHelper.sendActions(c, e)
            }
        }
    }
}