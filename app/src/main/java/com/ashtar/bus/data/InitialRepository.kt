package com.ashtar.bus.data

import android.util.Log
import androidx.room.withTransaction
import com.ashtar.bus.common.City
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.database.AppDatabase
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.model.RouteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface InitialRepository {
    suspend fun refreshRoute()
}

class InitialRepositoryImpl @Inject constructor(
    private val busApiService: BusApiService,
    private val database: AppDatabase,
    private val routeDao: RouteDao
) : InitialRepository {
    private val TAG = this::class.simpleName

    override suspend fun refreshRoute() {
        withContext(Dispatchers.IO) {
            joinAll(
                launch { refreshRouteByCity(City.Taipei) },
                launch { refreshRouteByCity(City.NewTaipei) }
            )
        }
    }

    private suspend fun refreshRouteByCity(city: City) {
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, null, e)
        }
    }
}