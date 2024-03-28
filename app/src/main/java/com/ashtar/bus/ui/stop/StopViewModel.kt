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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
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

    var uiState by mutableStateOf(UiState.Loading)
        private set

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
            if (uiState != UiState.Loaded) {
                try {
                    route = routeRepository.getRoute(routeId)
                    stopOfRouteList = stopRepository.getStopOfRouteList(route)
                    uiState = UiState.Loaded
                    countdown(REFRESH_INTERVAL)
                } catch (e: IOException) {
                    uiState = UiState.Error
                    dialog = Dialog.Error
                    return@launch
                }
            }
            while (true) {
                try {
                    stopOfRouteList = stopRepository.updateRouteEstimatedTime(route, stopOfRouteList)
                    uiState = UiState.Loaded
                    countdown(REFRESH_INTERVAL)
                } catch (e: IOException) {
                    uiState = UiState.LoadedOffline
                    countdown(5)
                }
            }
        }
    }

    fun stopRefreshJob() {
        refreshJob.cancel()
    }

    private suspend fun countdown(seconds: Int) {
        for (i in seconds downTo 1) {
            nextUpdateIn = i
            delay(1000)
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

enum class UiState {
    Loading,
    Error,
    Loaded,
    LoadedOffline
}

sealed interface Dialog {
    data object None : Dialog
    data object Error : Dialog
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