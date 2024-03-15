package com.ashtar.bus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ashtar.bus.common.City
import com.squareup.moshi.Json

data class Route(
    val id: String,
    val name: String,
    val city: City,
    val departureStop: String,
    val destinationStop: String,
    val marked: Boolean
)

data class RouteJson(
    @field:Json(name = "RouteUID")
    val id: String,
    @field:Json(name = "RouteName")
    val routeName: RouteName,
    @field:Json(name = "City")
    val city: City,
    @field:Json(name = "DepartureStopNameZh")
    val departureStop: String,
    @field:Json(name = "DestinationStopNameZh")
    val destinationStop: String,
    @field:Json(name = "UpdateTime")
    val updateTime: String
) {
    data class RouteName(
        @field:Json(name = "Zh_tw")
        val name: String
    )
}

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("route_name")
    val routeName: String,
    @ColumnInfo("city")
    val city: City,
    @ColumnInfo("departure_stop")
    val departureStop: String,
    @ColumnInfo("destination_stop")
    val destinationStop: String,
    @ColumnInfo("update_time")
    val updateTime: String,
    @ColumnInfo("marked")
    var marked: Boolean = false
)

fun List<RouteEntity>.toRouteList(): List<Route> = this.map {
    Route(
        id = it.id,
        name = it.routeName,
        city = it.city,
        departureStop = it.departureStop,
        destinationStop = it.destinationStop,
        marked = it.marked
    )
}