package com.ashtar.bus.data

import android.util.Log
import com.ashtar.bus.common.City
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.RouteEntity
import com.ashtar.bus.model.toRouteList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BusRepository {
    suspend fun searchRoute(query: String): List<Route>
    suspend fun refreshRoute()
}

class BusRepositoryImpl @Inject constructor(
    private val busApiService: BusApiService,
    private val routeDao: RouteDao
) : BusRepository {
    override suspend fun searchRoute(query: String): List<Route> = withContext(Dispatchers.IO) {
        val result = if (query.isEmpty()) {
            emptyList()
        } else {
            routeDao.getList("%$query%")
                .filter { it.routeName.matches(Regex("^[^0-9]*$query.*", RegexOption.IGNORE_CASE)) }
                .toRouteList()
        }
        result
    }

    override suspend fun refreshRoute() = withContext(Dispatchers.IO) {
        joinAll(
            launch { refreshRouteByCity(City.Taipei) },
            launch { refreshRouteByCity(City.NewTaipei) }
        )
    }

    private suspend fun refreshRouteByCity(city: City) {
        try {
            val updateTime = routeDao.getUpdateTime(city)
            val response = busApiService.getRouteList(city.name, updateTime)
            if (response.isSuccessful) {
                val routeList = response.body()!!
                    .sortedBy { it.routeName.name }
                    .map {
                        RouteEntity(
                            id = it.id,
                            routeName = it.routeName.name,
                            city = it.city,
                            departureStop = it.departureStop,
                            destinationStop = it.destinationStop,
                            updateTime = it.updateTime
                        )
                    }
                routeDao.refreshByCity(city, routeList)
            }
        } catch (e: Exception) {
            Log.e("refresh error", null, e)
        }
    }
}