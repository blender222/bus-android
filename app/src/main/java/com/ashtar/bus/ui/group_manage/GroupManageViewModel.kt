package com.ashtar.bus.ui.group_manage

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

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.deleteGroup(group)
        }
    }
}