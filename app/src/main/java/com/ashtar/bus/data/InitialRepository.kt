package com.ashtar.bus.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.room.withTransaction
import com.ashtar.bus.common.City
import com.ashtar.bus.common.SessionManager
import com.ashtar.bus.data.dao.GroupDao
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.database.AppDatabase
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.model.GroupEntity
import com.ashtar.bus.model.RouteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface InitialRepository {
    suspend fun initial()
}

class InitialRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val busApiService: BusApiService,
    private val dataStore: DataStore<Preferences>,
    private val database: AppDatabase,
    private val groupDao: GroupDao,
    private val routeDao: RouteDao
) : InitialRepository {
    override suspend fun initial() {
        withContext(Dispatchers.IO) {
            prepopulateData()
            sessionManager.initToken()
            refreshRoute()
        }
    }

    private suspend fun prepopulateData() {
        val key = booleanPreferencesKey("first_open")
        val firstOpen = dataStore.data.map { it[key] }.first()
        if (firstOpen == false) {
            return
        }
        listOf(
            GroupEntity(sort = 1, name = "上班"),
            GroupEntity(sort = 2, name = "下班"),
            GroupEntity(sort = 3, name = "出去玩")
        ).forEach {
            groupDao.insert(it)
        }
        dataStore.edit {
            it[key] = false
        }
    }

    private suspend fun refreshRoute() {
        withContext(Dispatchers.IO) {
            joinAll(
                launch { refreshRouteByCity(City.Taipei) },
                launch { refreshRouteByCity(City.NewTaipei) }
            )
        }
    }

    private suspend fun refreshRouteByCity(city: City) {
        val updateTime = routeDao.getUpdateTime(city)
        val response = busApiService.getRouteList(city.name, updateTime)
        if (!response.isSuccessful) {
            return
        }
        database.withTransaction {
            val markedIdList = routeDao.getMarkedIdList()
            val routeList = response.body()!!
                .sortedBy { it.routeName.name }
                .map {
                    RouteEntity(
                        id = it.id,
                        routeName = it.routeName.name,
                        city = it.city,
                        departureStop = it.departureStop,
                        destinationStop = it.destinationStop,
                        updateTime = it.updateTime,
                        marked = markedIdList.contains(it.id)
                    )
                }
            routeDao.deleteAll(city)
            routeDao.insertAll(routeList)
        }
    }
}