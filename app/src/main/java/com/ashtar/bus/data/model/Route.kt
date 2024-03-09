package com.ashtar.bus.data.model

import com.ashtar.bus.common.City
import com.squareup.moshi.Json

data class Route(
    @field:Json(name = "RouteName")
    val routeName: RouteName,
    @field:Json(name = "City")
    val city: City,
    @field:Json(name = "DepartureStopNameZh")
    val departureStop: String,
    @field:Json(name = "DestinationStopNameZh")
    val destinationStop: String,
    @field:Json(name = "UpdateTime")
    val updateTime: String,
)

data class RouteName(
    @field:Json(name = "Zh_tw")
    val name: String,
)