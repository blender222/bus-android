package com.ashtar.bus.data

import androidx.room.withTransaction
import com.ashtar.bus.common.StopStatus
import com.ashtar.bus.common.secondsToMinutes
import com.ashtar.bus.data.dao.MarkedStopDao
import com.ashtar.bus.data.database.AppDatabase
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.MarkedStop
import com.ashtar.bus.model.MarkedStopEntity
import com.ashtar.bus.model.Stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MarkedStopRepository {
    fun getByGroupIdFlow(groupId: Int): Flow<List<MarkedStop>>

    fun getAllGroupWithMarkedStopList(): Flow<List<Pair<Group, List<MarkedStop>>>>

    suspend fun insertMarkedStop(routeId: String, groupId: Int, stop: Stop)

    suspend fun insertGroupWithMarkedStop(routeId: String, name: String, stop: Stop)

    suspend fun updateEstimatedTime()

    suspend fun updateOffline()

    suspend fun updateSort(idList: List<Int>)

    suspend fun deleteById(id: Int)
}

class MarkedStopRepositoryImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    private val busApiService: BusApiService,
    private val database: AppDatabase,
    private val markedStopDao: MarkedStopDao
) : MarkedStopRepository {
    override fun getByGroupIdFlow(groupId: Int): Flow<List<MarkedStop>> {
        return markedStopDao.getByGroupIdFlow(groupId)
    }

    override fun getAllGroupWithMarkedStopList(): Flow<List<Pair<Group, List<MarkedStop>>>> {
        val groupFlow = groupRepository.getAllGroup()
        val markedStopFlow = markedStopDao.getAllFlow()
        return combine(groupFlow, markedStopFlow) { groupList, markedStopList ->
            groupList.map { group ->
                group to markedStopList.filter { it.groupId == group.id }
            }
        }
    }

    override suspend fun insertMarkedStop(routeId: String, groupId: Int, stop: Stop) {
        database.withTransaction {
            val newSort = markedStopDao.getMaxSort(groupId) + 1
            markedStopDao.insert(
                MarkedStopEntity(
                    groupId = groupId,
                    sort = newSort,
                    routeId = routeId,
                    direction = stop.direction,
                    stopId = stop.id,
                    stopName = stop.name
                )
            )
        }
    }

    override suspend fun insertGroupWithMarkedStop(routeId: String, name: String, stop: Stop) {
        database.withTransaction {
            val groupId = groupRepository.insertGroup(name)
            insertMarkedStop(routeId, groupId, stop)
        }
    }

    override suspend fun updateEstimatedTime() {
        withContext(Dispatchers.IO) {
            val list: List<MarkedStop> = markedStopDao.getAllFlow().first()
            val newList = list
                .map { item: MarkedStop ->
                    async {
                        val data = busApiService.getEstimatedTimeList(
                            city = item.city.name,
                            routeName = item.routeName,
                            filter = "RouteUID eq '${item.routeId}' and StopUID eq '${item.stopId}'"
                        ).firstOrNull()
                        object {
                            val id = item.id
                            val stopStatus = data?.stopStatus ?: StopStatus.NotDepart.code
                            val estimatedMin = data?.estimatedTime.secondsToMinutes()
                        }
                    }
                }
                .awaitAll()
            database.withTransaction {
                newList.forEach { item ->
                    launch {
                        markedStopDao.updateStatus(
                            id = item.id,
                            stopStatus = item.stopStatus,
                            estimatedMin = item.estimatedMin
                        )
                    }
                }
            }
        }
    }

    override suspend fun updateOffline() {
        markedStopDao.updateOffline()
    }

    override suspend fun updateSort(idList: List<Int>) {
        database.withTransaction {
            coroutineScope {
                idList.forEachIndexed { index, id ->
                    launch {
                        markedStopDao.updateSort(id, index + 1)
                    }
                }
            }
        }
    }

    override suspend fun deleteById(id: Int) {
        markedStopDao.deleteById(id)
    }
}