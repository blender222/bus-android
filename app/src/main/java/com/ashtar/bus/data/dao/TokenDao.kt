package com.ashtar.bus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ashtar.bus.data.model.TokenEntity

@Dao
interface TokenDao {
    @Query("SELECT access_token FROM token LIMIT 1")
    suspend fun getToken(): List<String>

    @Insert
    suspend fun insert(item: TokenEntity)

    @Update
    suspend fun update(item: TokenEntity)
}