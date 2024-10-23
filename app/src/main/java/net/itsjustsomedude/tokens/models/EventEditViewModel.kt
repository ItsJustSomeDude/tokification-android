package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.updateInferredCoopValues
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.Calendar

class EventEditViewModel(
    // TODO: Find out if this is always present and needed.
    // I think the answer is "yes".
    private val coopId: Long,
    // If null, create event, otherwise edit.
    private val eventId: Long?,
    private val eventRepo: EventRepository,
    private val coopRepo: CoopRepository
) : ViewModel() {

    val event = MutableLiveData<Event>(null)
    val coop = liveData {
        val fetchedCoop = coopRepo.getCoop(coopId)
        emitSource(fetchedCoop)

        // TODO: I have no idea if this is good.
        // I do think it's pretty good... if it works...
        fetchedCoop.value?.let { co ->
            event.value?.let { ev ->
                updateEvent(
                    ev.copy(
//                        coop = co.name,
//                        kevId = co.contract,

                        // If user has already changed the value don't change it again.
                        count = if (ev.count != 0) ev.count
                        else if (co.sinkMode) 6
                        else 2,

                        // If user has already changed the value don't change it again.
                        person = if (ev.person != "") ev.person
                        // TODO: Boost Order next
                        else if (co.sinkMode) ""
                        else "Sink"
                    )
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            // TODO: Usage of update inferred values.
//            updateInferredCoopValues(coopId)

            if (eventId == null) event.value = Event(
                coop = coop.value?.name ?: "",
                kevId = coop.value?.contract ?: "",
                count = 6,
                direction = Event.DIRECTION_RECEIVED,
                time = Calendar.getInstance(),
                person = ""
            )
            else {
                // Fetch and Edit Event.
                event.value = eventRepo.getEventDirect(eventId)
                // TODO: Handle possible null events?
            }


        }
    }

    fun updateEvent(newEvent: Event) {
        event.value = newEvent
    }

    fun save() {
        event.value?.let { ev ->
            coop.value?.let {
                println("Saving!!!")
                val newEvent = ev.copy(
                    coop = it.name, kevId = it.contract
                )

                eventRepo.upsert(newEvent)

                viewModelScope.launch {
                    updateInferredCoopValues(newEvent)
                }
            }
        }
    }

    companion object {
        // TODO: ChatGPT doesn't like this, but I do... so...

        @Composable
        fun provide(key: String, coopId: Long, eventId: Long? = null): EventEditViewModel =
            koinViewModel(key = key) { parametersOf(coopId, eventId) }

    }
}