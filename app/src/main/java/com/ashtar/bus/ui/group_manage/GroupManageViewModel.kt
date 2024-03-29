package com.ashtar.bus.ui.group_manage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashtar.bus.data.GroupRepository
import com.ashtar.bus.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupManageViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    var blocking by mutableStateOf(false)

    val groupList: StateFlow<List<Group>> = groupRepository
        .getAllGroup()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun insertGroup(name: String) {
        viewModelScope.launch {
            groupRepository.insertGroup(name)
        }
    }

    fun updateGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.updateGroup(group)
        }
    }

    fun updateSort(list: List<Group>) {
        val newIdList = list.map { it.id }
        if (newIdList == groupList.value.map { it.id }) {
            return
        }
        blocking = true
        viewModelScope.launch {
            groupRepository.updateSort(newIdList)
            blocking = false
        }
    }

    fun deleteGroup(group: Group) {
        blocking = true
        viewModelScope.launch {
            groupRepository.deleteGroup(group)
            blocking = false
        }
    }
}