package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.Composable
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
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reports.DetailedReport
import net.itsjustsomedude.tokens.reports.SinkReport
import net.itsjustsomedude.tokens.updateInferredCoopValues
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

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
        // TODO: Usage of update inferred values.
        updateInferredCoopValues(coopId)
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
    
    val selectedEventId = MutableLiveData<Long?>(null)

    fun selectEvent(id: Long?) {
        selectedEventId.value = id
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

    fun copySinkReport() {
        coop.value?.let {
            val report = SinkReport().generate(coop = it, events = events.value ?: emptyList())

            clipboard.copyText(report)
        }
    }

    fun copyDetailedReport() {
        coop.value?.let {
            val report = DetailedReport().generate(coop = it, events = events.value ?: emptyList())

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

    companion object {
        @Composable
        fun provide(coopId: Long): CoopViewModel =
            koinViewModel(key = coopId.toString()) { parametersOf(coopId) }
    }
}