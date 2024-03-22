package com.ashtar.bus.data

import com.ashtar.bus.data.dao.GroupDao
import com.ashtar.bus.data.dao.MarkedStopDao
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.GroupEntity
import com.ashtar.bus.model.MarkedStop
import com.ashtar.bus.model.toEntity
import com.ashtar.bus.model.toGroupList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GroupRepository {
    fun getAllGroup(): Flow<List<Group>>

    fun getAllGroupWithMarkedStopList(): Flow<List<Pair<Group, List<MarkedStop>>>>

    suspend fun insertGroup(name: String)

    suspend fun updateGroup(group: Group)

    suspend fun deleteGroup(group: Group)
}

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val markedStopDao: MarkedStopDao
) : GroupRepository {
    override fun getAllGroup(): Flow<List<Group>> {
        return groupDao.getAll().map { it.toGroupList() }
    }

    override fun getAllGroupWithMarkedStopList(): Flow<List<Pair<Group, List<MarkedStop>>>> {
        val groupFlow = getAllGroup()
        val markedStopFlow = markedStopDao.getAll()
        return combine(groupFlow, markedStopFlow) { groupList, markedStopList ->
            groupList.map { group ->
                group to markedStopList.filter { it.groupId == group.id }
            }
        }
    }

    override suspend fun insertGroup(name: String) {
        groupDao.insert(GroupEntity(name = name))
    }

    override suspend fun updateGroup(group: Group) {
        groupDao.update(group.toEntity())
    }

    override suspend fun deleteGroup(group: Group) {
        groupDao.delete(group.toEntity())
    }
}