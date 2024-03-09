package com.ashtar.bus.data.network

import com.ashtar.bus.data.model.Route
import retrofit2.http.GET
import retrofit2.http.Path

interface BusApiService {
    @GET("v2/Bus/Route/City/{City}")
    suspend fun getRouteList(@Path("City") city: String): List<Route>
}