package com.ashtar.bus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ashtar.bus.common.City
import com.ashtar.bus.model.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM route WHERE route_name LIKE :query ORDER BY route_name")
    suspend fun getList(query: String): List<RouteEntity>

    @Query("SELECT * FROM route WHERE marked ORDER BY route_name")
    fun getMarkedList(): Flow<List<RouteEntity>>

    @Query("SELECT id FROM route WHERE marked")
    suspend fun getMarkedIdList(): List<String>

    @Query("SELECT * FROM route WHERE id = :id")
    suspend fun get(id: String): RouteEntity

    @Query("SELECT update_time FROM route WHERE city = :city LIMIT 1")
    suspend fun getUpdateTime(city: City): String

    @Insert
    suspend fun insertAll(items: List<RouteEntity>)

    @Update
    suspend fun update(item: RouteEntity)

    @Query("DELETE FROM route WHERE city = :city")
    suspend fun deleteAll(city: City)
}