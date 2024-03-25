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
            g.id groupId,
            r.id routeId,
            r.route_name routeName,
            CASE ms.direction
                WHEN 0 THEN r.destination_stop
                WHEN 1 THEN r.departure_stop
            END destination,
            r.city city,
            ms.stop_id stopId,
            ms.stop_name stopName,
            ms.stop_status stopStatus,
            ms.estimated_min estimatedMin
        FROM marked_stop ms
        INNER JOIN `group` g ON g.id = ms.group_id
        INNER JOIN route r ON r.id = ms.route_id
        ORDER BY g.sort, ms.sort
    """)
    fun getAllFlow(): Flow<List<MarkedStop>>

    @Query("""
        SELECT ms.id id,
            ms.group_id groupId,
            ms.sort sort,
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
        WHERE ms.group_id = :groupId
        ORDER BY ms.sort
    """)
    fun getByGroupIdFlow(groupId: Int): Flow<List<MarkedStop>> 

    @Query("SELECT MAX(sort) FROM marked_stop WHERE group_id = :groupId")
    suspend fun getMaxSort(groupId: Int): Int

    @Insert
    suspend fun insert(item: MarkedStopEntity)

    @Query("""
        UPDATE marked_stop SET
            stop_status = :stopStatus,
            estimated_min = :estimatedMin
        WHERE id = :id
    """)
    suspend fun updateStatus(id: Int, stopStatus: Int?, estimatedMin: Int?)

    @Query("UPDATE marked_stop SET sort = :sort WHERE id = :id")
    suspend fun updateSort(id: Int, sort: Int)

    @Query("DELETE FROM marked_stop WHERE id = :id")
    suspend fun deleteById(id: Int)
}