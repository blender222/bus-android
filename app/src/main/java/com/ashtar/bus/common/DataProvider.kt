package com.ashtar.bus.common

import com.ashtar.bus.model.MarkedStop

object DataProvider {
    val markedStopList = listOf(
        MarkedStop(
            id = 1,
            routeName = "307",
            destination = "板橋",
            city = City.Taipei,
            stopName = "萬大路",
            stopStatus = StopStatus.Normal,
            estimatedMin = 5
        ),
        MarkedStop(
            id = 2,
            routeName = "307",
            destination = "撫遠街",
            city = City.Taipei,
            stopName = "西藏路口",
            stopStatus = StopStatus.TrafficControl,
            estimatedMin = null
        ),
        MarkedStop(
            id = 3,
            routeName = "307",
            destination = "板橋",
            city = City.Taipei,
            stopName = "聯合醫院和平區",
            stopStatus = StopStatus.Normal,
            estimatedMin = 0
        ),
    )
}