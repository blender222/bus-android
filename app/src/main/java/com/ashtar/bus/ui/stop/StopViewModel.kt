package com.ashtar.bus.ui.stop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.data.GroupRepository
import com.ashtar.bus.data.MarkedStopRepository
import com.ashtar.bus.data.RouteRepository
import com.ashtar.bus.data.StopRepository
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.Stop
import com.ashtar.bus.model.StopOfRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val REFRESH_INTERVAL = 20

@HiltViewModel
class StopViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routeRepository: RouteRepository,
    private val groupRepository: GroupRepository,
    private val stopRepository: StopRepository,
    private val markedStopRepository: MarkedStopRepository
) : ViewModel() {
    private val routeId: String = checkNotNull(savedStateHandle["routeId"])

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var route: Route by mutableStateOf(Route())
        private set

    var stopOfRouteList: List<StopOfRoute> by mutableStateOf(emptyList())
        private set

    var nextUpdateIn: Int by mutableIntStateOf(REFRESH_INTERVAL)
        private set

    var dialog: Dialog by mutableStateOf(Dialog.None)
        private set

    private lateinit var refreshJob: Job

    fun startRefreshJob() {
        nextUpdateIn = REFRESH_INTERVAL
        refreshJob = viewModelScope.launch {
            try {
                if (_uiState.value.isLoading) {
                    route = routeRepository.getRoute(routeId)
                    stopOfRouteList = stopRepository.getStopOfRouteList(route)
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    countdown()
                }
                while (true) {
                    stopOfRouteList = stopRepository.updateRouteEstimatedTime(route, stopOfRouteList)
                    countdown()
                }
            } catch (_: Exception) {}
        }
    }

    fun stopRefreshJob() {
        refreshJob.cancel()
    }

    private suspend fun countdown() {
        for (i in REFRESH_INTERVAL downTo 0) {
            delay(1000)
            nextUpdateIn = i
        }
    }

    fun openMenuDialog(stop: Stop) {
        dialog = Dialog.Menu(stop)
    }

    fun openAddToGroupDialog(stop: Stop) {
        viewModelScope.launch {
            val groupList = groupRepository.getAllGroup().first()
            dialog = Dialog.AddToGroup(
                stop = stop,
                groupList = groupList
            )
        }
    }

    fun openNewGroupDialog(stop: Stop) {
        dialog = Dialog.NewGroup(stop)
    }

    fun closeDialog() {
        dialog = Dialog.None
    }

    fun insertMarkedStop(group: Group, stop: Stop) {
        viewModelScope.launch {
            markedStopRepository.insertMarkedStop(routeId, group.id, stop)
        }
    }

    fun insertGroupWithMarkedStop(name: String, stop: Stop) {
        viewModelScope.launch {
            markedStopRepository.insertGroupWithMarkedStop(routeId, name, stop)
        }
    }
}

data class UiState(
    val isLoading: Boolean = true
)

sealed interface Dialog {
    data object None : Dialog
    data class Menu(
        val stop: Stop
    ) : Dialog
    data class AddToGroup(
        val stop: Stop,
        val groupList: List<Group>
    ) : Dialog
    data class NewGroup(
        val stop: Stop
    ) : Dialog
}