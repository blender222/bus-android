package com.ashtar.bus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ashtar.bus.model.MarkedStop
import com.ashtar.bus.model.MarkedStopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkedStopDao {
    @Query("""
        SELECT ms.id id,
            ms.group_id groupId,
            r.id routeId,
            r.route_name routeName,
            CASE ms.direction
                WHEN 0 THEN r.destination_stop
                WHEN 1 THEN r.departure_stop
            END destination,
            r.city city,
            ms.stop_id stopId,
            ms.stop_name stopName
        FROM marked_stop ms
        INNER JOIN route r ON r.id = ms.route_id
        ORDER BY id
    """)
    fun getAll(): Flow<List<MarkedStop>>

    @Insert
    suspend fun insert(item: MarkedStopEntity)
}