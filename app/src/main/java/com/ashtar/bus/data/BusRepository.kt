package com.ashtar.bus.data

import com.ashtar.bus.common.City
import com.ashtar.bus.data.model.Route
import com.ashtar.bus.data.network.BusApiService
import javax.inject.Inject

interface BusRepository {
    suspend fun getRouteList(): List<Route>
}

class BusRepositoryImpl @Inject constructor(
    private val busApiService: BusApiService
) : BusRepository {
    override suspend fun getRouteList(): List<Route> {
        try {
            return busApiService.getRouteList(City.Taipei.name)
        } catch (e: Exception) {
            throw e
        }
    }
}