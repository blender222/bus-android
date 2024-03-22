package com.ashtar.bus.data

import androidx.room.withTransaction
import com.ashtar.bus.common.StopStatus
import com.ashtar.bus.data.dao.GroupDao
import com.ashtar.bus.data.dao.MarkedStopDao
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.database.AppDatabase
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.model.EstimatedTimeJson
import com.ashtar.bus.model.GroupEntity
import com.ashtar.bus.model.MarkedStopEntity
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.Stop
import com.ashtar.bus.model.StopJson
import com.ashtar.bus.model.StopOfRoute
import com.ashtar.bus.model.VehicleOfStopJson
import com.ashtar.bus.model.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface StopRepository {
    suspend fun getRoute(routeId: String): Route

    suspend fun getStopOfRouteList(route: Route): List<StopOfRoute>

    suspend fun updateEstimatedTime(route: Route, stopOfRouteList: List<StopOfRoute>): List<StopOfRoute>

    suspend fun insertMarkedStop(routeId: String, groupId: Int, stop: Stop)

    suspend fun insertGroupWithMarkedStop(routeId: String, name: String, stop: Stop)
}

class StopRepositoryImpl @Inject constructor(
    private val busApiService: BusApiService,
    private val database: AppDatabase,
    private val routeDao: RouteDao,
    private val groupDao: GroupDao,
    private val markedStopDao: MarkedStopDao
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
            val vehicleOfRouteDeferred = async {
                getVehicleOfRoute(route)
            }
            val stopOfRouteJsonList = stopOfRouteDeferred.await()
            val estimatedTimeJsonList = estimatedTimeDeferred.await()
            val vehicleOfRouteJsonList = vehicleOfRouteDeferred.await()
            stopOfRouteJsonList
                .distinctBy { it.direction }
                .sortedBy { it.direction }
                .map { stopOfRouteJson ->
                    StopOfRoute(
                        destination = when (stopOfRouteJson.direction) {
                            0 -> route.destinationStop
                            1 -> route.departureStop
                            else -> ""
                        },
                        stops = stopOfRouteJson.stops.map { item: StopJson ->
                            val stopInfo = estimatedTimeJsonList.find { info -> info.stopId == item.id }
                            val plateNumbs = vehicleOfRouteJsonList
                                .filter { it.stopId == item.id }
                                .map { it.plateNumb }
                            if (stopInfo != null) {
                                Stop(
                                    id = item.id,
                                    name = item.stopName.name,
                                    direction = stopOfRouteJson.direction,
                                    stopStatus = StopStatus.fromCode(stopInfo.stopStatus),
                                    estimatedMin = stopInfo.estimatedTime.let { seconds ->
                                        if (seconds != null) (seconds / 60) else null
                                    },
                                    plateNumbs = plateNumbs
                                )
                            } else {
                                Stop(
                                    id = item.id,
                                    name = item.stopName.name,
                                    direction = stopOfRouteJson.direction,
                                    stopStatus = StopStatus.Normal,
                                    estimatedMin = null,
                                    plateNumbs = plateNumbs
                                )
                            }
                        }
                    )
                }
        }
    }

    override suspend fun updateEstimatedTime(route: Route, stopOfRouteList: List<StopOfRoute>): List<StopOfRoute> {
        return withContext(Dispatchers.IO) {
            val estimatedTimeDeferred = async {
                getEstimatedTimeList(route)
            }
            val vehicleOfRouteDeferred = async {
                getVehicleOfRoute(route)
            }
            val estimatedTimeJsonList = estimatedTimeDeferred.await()
            val vehicleOfRouteJsonList = vehicleOfRouteDeferred.await()
            stopOfRouteList.map { stopOfRoute ->
                val stopList = stopOfRoute.stops.map inner@{ stop ->
                    val data = estimatedTimeJsonList.find { it.stopId == stop.id } ?: return@inner stop
                    val plateNumbs = vehicleOfRouteJsonList
                        .filter { it.stopId == stop.id }
                        .map { it.plateNumb }
                    stop.copy(
                        stopStatus = StopStatus.fromCode(data.stopStatus),
                        estimatedMin = data.estimatedTime.let { seconds ->
                            if (seconds != null) (seconds / 60) else null
                        },
                        plateNumbs = plateNumbs
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

    private suspend fun getVehicleOfRoute(route: Route): List<VehicleOfStopJson> {
        return busApiService.getVehicleOfStop(
            city = route.city.name,
            routeName = route.name,
            filter = "RouteUID eq '${route.id}' and DutyStatus ne 2"
        )
    }

    override suspend fun insertMarkedStop(routeId: String, groupId: Int, stop: Stop) {
        markedStopDao.insert(
            MarkedStopEntity(
                groupId = groupId,
                routeId = routeId,
                direction = stop.direction,
                stopId = stop.id,
                stopName = stop.name
            )
        )
    }

    override suspend fun insertGroupWithMarkedStop(routeId: String, name: String, stop: Stop) {
        database.withTransaction {
            val groupId = groupDao.insert(GroupEntity(name = name)).toInt()
            insertMarkedStop(routeId, groupId, stop)
        }
    }
}