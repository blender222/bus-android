package com.ashtar.bus.data

import androidx.room.withTransaction
import com.ashtar.bus.data.dao.GroupDao
import com.ashtar.bus.data.database.AppDatabase
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.GroupEntity
import com.ashtar.bus.model.toEntity
import com.ashtar.bus.model.toGroupList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GroupRepository {
    fun getAllGroup(): Flow<List<Group>>

    suspend fun insertGroup(name: String): Int

    suspend fun updateGroup(group: Group)

    suspend fun updateSort(idList: List<Int>)

    suspend fun deleteGroup(group: Group)
}

class GroupRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val groupDao: GroupDao
) : GroupRepository {
    override fun getAllGroup(): Flow<List<Group>> {
        return groupDao.getAll().map { it.toGroupList() }
    }

    override suspend fun insertGroup(name: String): Int {
        return database.withTransaction {
            val newSort = groupDao.getMaxSort() + 1
            groupDao.insert(
                GroupEntity(
                    sort = newSort,
                    name = name
                )
            ).toInt()
        }
    }

    override suspend fun updateGroup(group: Group) {
        groupDao.update(group.toEntity())
    }

    override suspend fun updateSort(idList: List<Int>) {
        database.withTransaction {
            coroutineScope {
                idList.forEachIndexed { index, id ->
                    launch {
                        groupDao.updateSort(id, index + 1)
                    }
                }
            }
        }
    }

    override suspend fun deleteGroup(group: Group) {
        groupDao.delete(group.toEntity())
    }
}