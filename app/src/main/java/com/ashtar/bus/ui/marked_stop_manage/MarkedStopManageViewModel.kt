package com.ashtar.bus.ui.marked_stop_manage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.data.MarkedStopRepository
import com.ashtar.bus.model.MarkedStop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarkedStopManageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val markedStopRepository: MarkedStopRepository
) : ViewModel() {
    private val groupId: Int = checkNotNull(savedStateHandle["groupId"])

    var blocking by mutableStateOf(false)

    val stopList: StateFlow<List<MarkedStop>> = markedStopRepository
        .getByGroupIdFlow(groupId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun updateSort(list: List<MarkedStop>) {
        val newIdList = list.map { it.id }
        if (newIdList == stopList.value.map { it.id }) {
            return
        }
        blocking = true
        viewModelScope.launch {
            markedStopRepository.updateSort(newIdList)
            blocking = false
        }
    }

    fun deleteMarkedStop(id: Int) {
        blocking = true
        viewModelScope.launch {
            markedStopRepository.deleteById(id)
            blocking = false
        }
    }
}