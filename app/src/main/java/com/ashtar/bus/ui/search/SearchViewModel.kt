package com.ashtar.bus.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.data.BusRepository
import com.ashtar.bus.model.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val busRepository: BusRepository
) : ViewModel() {
    var query by mutableStateOf("")
        private set

    var routeList: List<Route> by mutableStateOf(emptyList())
        private set

    fun onSearchInput(input: String) {
        query = input
        viewModelScope.launch {
            routeList = busRepository.searchRoute(input)
        }
    }
}