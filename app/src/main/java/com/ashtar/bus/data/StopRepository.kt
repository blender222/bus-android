package com.ashtar.bus.data

import com.ashtar.bus.common.StopStatus
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.model.EstimatedTimeJson
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.Stop
import com.ashtar.bus.model.StopOfRoute
import com.ashtar.bus.model.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface StopRepository {
    suspend fun getRoute(routeId: String): Route

    suspend fun getStopOfRouteList(route: Route): List<StopOfRoute>

    suspend fun updateEstimatedTime(route: Route, stopOfRouteList: List<StopOfRoute>): List<StopOfRoute>
}

class StopRepositoryImpl @Inject constructor(
    private val busApiService: BusApiService,
    private val routeDao: RouteDao
) : StopRepository {
    override suspend fun getRoute(routeId: String): Route {
        return routeDao.get(routeId).toRoute()
    }

    override suspend fun getStopOfRouteList(route: Route): List<StopOfRoute> {
        return withContext(Dispatchers.IO) {
            val stopOfRouteDeferred = async {
                busApiService.getStopOfRouteList(
                    city = route.city.name,
                    routeName = route.name,
                    filter = "RouteUID eq '${route.id}'"
                )
            }
            val estimatedTimeDeferred = async {
                getEstimatedTimeList(route)
            }
            val stopOfRouteJsonList = stopOfRouteDeferred.await()
            val estimatedTimeJsonList = estimatedTimeDeferred.await()
            stopOfRouteJsonList
                .sortedBy { it.direction }
                .map {
                    StopOfRoute(
                        destination = when (it.direction) {
                            0 -> route.destinationStop
                            1 -> route.departureStop
                            else -> ""
                        },
                        stops = it.stops.map { stop ->
                            val stopInfo = estimatedTimeJsonList.find { info -> info.stopId == stop.id } 
                            if (stopInfo != null) {
                                Stop(
                                    id = stop.id,
                                    name = stop.stopName.name,
                                    stopStatus = StopStatus.fromCode(stopInfo.stopStatus),
                                    estimatedMin = stopInfo.estimatedTime.let { seconds ->
                                        if (seconds != null) (seconds / 60) else null
                                    }
                                )
                            } else {
                                Stop(
                                    id = stop.id,
                                    name = stop.stopName.name,
                                    stopStatus = StopStatus.Normal,
                                    estimatedMin = null
                                )
                            }
                        }
                    )
                }
        }
    }

    override suspend fun updateEstimatedTime(route: Route, stopOfRouteList: List<StopOfRoute>): List<StopOfRoute> {
        return withContext(Dispatchers.IO) {
            val estimatedTimeJsonList = getEstimatedTimeList(route)
            stopOfRouteList.map { stopOfRoute ->
                val stopList = stopOfRoute.stops.map inner@{ stop ->
                    val data = estimatedTimeJsonList.find { it.stopId == stop.id } ?: return@inner stop
                    stop.copy(
                        stopStatus = StopStatus.fromCode(data.stopStatus),
                        estimatedMin = data.estimatedTime.let { seconds ->
                            if (seconds != null) (seconds / 60) else null
                        }
                    )
                }
                stopOfRoute.copy(
                    stops = stopList
                )
            }
        }
    }

    private suspend fun getEstimatedTimeList(route: Route): List<EstimatedTimeJson> {
        return busApiService.getEstimatedTimeList(
            city = route.city.name,
            routeName = route.name,
            filter = "RouteUID eq '${route.id}'"
        )
    }
}