package com.ashtar.bus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ashtar.bus.common.City

data class MarkedStop(
    val id: Int = 0,
    val groupId: Int = 0,
    val routeId: Int = 0,
    val routeName: String,
    val destination: String,
    val city: City,
    val stopId: Int = 0,
    val stopName: String
)

@Entity(
    tableName = "marked_stop",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MarkedStopEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("group_id")
    val groupId: Int,
    @ColumnInfo("route_id")
    val routeId: String,
    @ColumnInfo("direction")
    val direction: Int,
    @ColumnInfo("stop_id")
    val stopId: String,
    @ColumnInfo("stop_name")
    val stopName: String
)