package com.ashtar.bus.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ashtar.bus.model.TokenEntity

@Dao
interface TokenDao {
    @Query("SELECT access_token FROM token LIMIT 1")
    suspend fun getToken(): List<String>

    @Upsert
    suspend fun upsert(item: TokenEntity)
}