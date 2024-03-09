package com.ashtar.bus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class TokenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("access_token")
    val accessToken: String,
)