package com.ashtar.bus.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    var state by mutableStateOf(SearchState.Initial)
        private set

    var query by mutableStateOf(TextFieldValue(""))
        private set

    var markedList: List<Route> by mutableStateOf(emptyList())
        private set

    var searchedList: List<Route> by mutableStateOf(emptyList())
        private set

    init {
        getMarkedList()
    }

    private fun getMarkedList() {
        viewModelScope.launch {
            busRepository.getMarkedList().collect {
                markedList = it
            }
        }
    }

    fun getByKeyboard(value: TextFieldValue) {
        query = value
        search(query.text)
    }

    fun getByReplace(value: String) {
        query = query.copy(
            text = value,
            selection = TextRange(value.length)
        )
        search(query.text)
    }

    fun getByInput(input: String) {
        query = query.copy(
            text = StringBuilder(query.text).insert(query.selection.start, input).toString(),
            selection = TextRange(query.selection.start + input.length)
        )
        search(query.text)
    }

    fun toggleMarked(route: Route) {
        viewModelScope.launch {
            busRepository.toggleMarked(route)
            search()
        }
    }

    private fun search(text: String = query.text) {
        viewModelScope.launch {
            searchedList = busRepository.searchRoute(text)
            state = if (text.isNotEmpty() && searchedList.isEmpty()) {
                SearchState.NoResult
            } else {
                SearchState.Initial
            }
        }
    }
}

enum class SearchState {
    Initial,
    NoResult
}