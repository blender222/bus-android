package com.ashtar.bus.ui.stop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.data.StopRepository
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.StopOfRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val REFRESH_INTERVAL = 20

@HiltViewModel
class StopViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stopRepository: StopRepository
) : ViewModel() {
    private val routeId: String = checkNotNull(savedStateHandle["routeId"])

    var state by mutableStateOf(UiState.Loading)
        private set

    var route: Route by mutableStateOf(Route())
        private set

    var stopOfRouteList: List<StopOfRoute> by mutableStateOf(emptyList())
        private set

    var nextUpdateIn: Int by mutableIntStateOf(REFRESH_INTERVAL)
        private set

    private lateinit var refreshJob: Job

    fun startRefreshJob() {
        nextUpdateIn = REFRESH_INTERVAL
        refreshJob = viewModelScope.launch {
            try {
                if (state == UiState.Loading) {
                    route = stopRepository.getRoute(routeId)
                    stopOfRouteList = stopRepository.getStopOfRouteList2(route)
                    state = UiState.Started
                    countdown()
                }
                while (true) {
                    stopOfRouteList = stopRepository.updateEstimatedTime(route, stopOfRouteList)
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
}

enum class UiState {
    Loading,
    Started
}