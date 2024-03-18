package com.ashtar.bus.data.network

import com.ashtar.bus.model.EstimatedTimeJson
import com.ashtar.bus.model.RouteJson
import com.ashtar.bus.model.StopOfRouteJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface BusApiService {
    @GET("v2/Bus/Route/City/{City}?\$select=RouteUID,RouteName,City,DepartureStopNameZh,DestinationStopNameZh,UpdateTime")
    suspend fun getRouteList(
        @Path("City") city: String,
        @Header("If-Modified-Since") updateTime: String? = null
    ): Response<List<RouteJson>>

    @GET("v2/Bus/StopOfRoute/City/{City}/{RouteName}?\$select=Direction,Stops&\$orderby=Direction")
    suspend fun getStopOfRouteList(
        @Path("City") city: String,
        @Path("RouteName") routeName: String,
        @Query("\$filter") filter: String
    ): List<StopOfRouteJson>

    @GET("v2/Bus/EstimatedTimeOfArrival/City/{City}/{RouteName}?\$select=StopUID,StopStatus,EstimateTime")
    suspend fun getEstimatedTimeList(
        @Path("City") city: String,
        @Path("RouteName") routeName: String,
        @Query("\$filter") filter: String
    ): List<EstimatedTimeJson>
}