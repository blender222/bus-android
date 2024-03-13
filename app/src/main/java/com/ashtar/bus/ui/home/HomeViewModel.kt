package com.ashtar.bus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.common.SessionManager
import com.ashtar.bus.data.BusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val busRepository: BusRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            sessionManager.initToken()
            busRepository.refreshRoute()
        }
    }
}