package com.ashtar.bus.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.data.MarkedStopRepository
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.MarkedStop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

const val REFRESH_INTERVAL = 20

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val markedStopRepository: MarkedStopRepository
) : ViewModel() {
    val groupList: StateFlow<List<Pair<Group, List<MarkedStop>>>?> = markedStopRepository
        .getAllGroupWithMarkedStopList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    var nextUpdateIn: Int by mutableIntStateOf(REFRESH_INTERVAL)
        private set

    private lateinit var refreshJob: Job

    fun startRefreshJob() {
        refreshJob = viewModelScope.launch {
            while (true) {
                try {
                    markedStopRepository.updateEstimatedTime()
                    countdown(REFRESH_INTERVAL)
                } catch (e: IOException) {
                    markedStopRepository.updateOffline()
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
}