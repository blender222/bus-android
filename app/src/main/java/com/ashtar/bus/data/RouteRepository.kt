package com.ashtar.bus.data

import androidx.room.withTransaction
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.database.AppDatabase
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.toRoute
import com.ashtar.bus.model.toRouteList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface RouteRepository {
    suspend fun searchRoute(query: String): List<Route>

    suspend fun getRoute(routeId: String): Route

    suspend fun getMarkedList(): Flow<List<Route>>

    suspend fun toggleMarked(route: Route)
}

class RouteRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val routeDao: RouteDao
) : RouteRepository {
    override suspend fun searchRoute(query: String): List<Route> = withContext(Dispatchers.IO) {
        val result = when {
            query.isEmpty() -> emptyList()
            query.matches(Regex("^[紅藍綠橘棕小].*")) -> {
                routeDao.getList("$query%")
                    .toRouteList()
            }
            query.contains("幹線") -> {
                routeDao.getList("%$query%")
                    .toRouteList()
            }
            else -> {
                routeDao.getList("%$query%")
                    .filter { it.routeName.matches(Regex("^[^0-9]*$query.*", RegexOption.IGNORE_CASE)) }
                    .toRouteList()
            }
        }
        result
    }

    override suspend fun getRoute(routeId: String): Route {
        return routeDao.get(routeId).toRoute()
    }

    override suspend fun getMarkedList(): Flow<List<Route>> = withContext(Dispatchers.IO) {
        routeDao.getMarkedList().map { it.toRouteList() }
    }

    override suspend fun toggleMarked(route: Route) = withContext(Dispatchers.IO) {
        database.withTransaction {
            val item = routeDao.get(route.id)
            item.marked = !route.marked
            routeDao.update(item)
        }
    }
}