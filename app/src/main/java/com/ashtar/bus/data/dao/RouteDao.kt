package com.ashtar.bus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ashtar.bus.common.City
import com.ashtar.bus.model.RouteEntity

@Dao
interface RouteDao {
    @Query("SELECT * FROM route WHERE route_name LIKE :query ORDER BY route_name")
    suspend fun getList(query: String): List<RouteEntity>

    @Query("SELECT update_time FROM route WHERE city = :city LIMIT 1")
    suspend fun getUpdateTime(city: City): String

    @Insert
    suspend fun insertAll(items: List<RouteEntity>)

    @Query("DELETE FROM route WHERE city = :city")
    suspend fun deleteAll(city: City)

    @Transaction
    suspend fun refreshByCity(city: City, items: List<RouteEntity>) {
        deleteAll(city)
        insertAll(items)
    }
}