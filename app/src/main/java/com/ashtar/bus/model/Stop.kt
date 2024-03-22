package com.ashtar.bus.model

import com.ashtar.bus.common.StopStatus
import com.squareup.moshi.Json

data class StopOfRoute(
    val destination: String,
    val stops: List<Stop>
)

data class Stop(
    val id: String,
    val name: String,
    val direction: Int,
    val stopStatus: StopStatus,
    val estimatedMin: Int?,
    val plateNumbs: List<String>
)

data class StopOfRouteJson(
    @field:Json(name = "Direction")
    val direction: Int,
    @field:Json(name = "Stops")
    val stops: List<StopJson>
)

data class StopJson(
    @field:Json(name = "StopUID")
    val id: String,
    @field:Json(name = "StopName")
    val stopName: StopName
) {
    data class StopName(
        @field:Json(name = "Zh_tw")
        val name: String
    )
}

data class EstimatedTimeJson(
    @field:Json(name = "StopUID")
    val stopId: String,
    @field:Json(name = "StopStatus")
    val stopStatus: Int,
    @field:Json(name = "EstimateTime")
    val estimatedTime: Int?
)

data class VehicleOfStopJson(
    @field:Json(name = "StopUID")
    val stopId: String,
    @field:Json(name = "PlateNumb")
    val plateNumb: String
)