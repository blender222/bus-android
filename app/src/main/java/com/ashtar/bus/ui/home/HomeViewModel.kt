package com.ashtar.bus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.common.SessionManager
import com.ashtar.bus.data.GroupRepository
import com.ashtar.bus.data.RouteRepository
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.MarkedStop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val routeRepository: RouteRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.initToken()
            routeRepository.refreshRoute()
            groupRepository
                .getAllGroupWithMarkedStopList()
                .collect { list ->
                    _uiState.update {
                        UiState.Success(groupList = list)
                    }
                }
        }
    }
}

sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val groupList: List<Pair<Group, List<MarkedStop>>>
    ) : UiState
}