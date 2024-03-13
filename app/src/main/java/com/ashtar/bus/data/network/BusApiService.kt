package com.ashtar.bus.data.network

import com.ashtar.bus.model.RouteJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface BusApiService {
    @GET("v2/Bus/Route/City/{City}?\$select=RouteUID,RouteName,City,DepartureStopNameZh,DestinationStopNameZh,UpdateTime")
    suspend fun getRouteList(
        @Path("City") city: String,
        @Header("If-Modified-Since") updateTime: String? = null
    ): Response<List<RouteJson>>
}