package com.ashtar.bus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class TokenJson(
    @field:Json(name = "access_token")
    val accessToken: String,
    @field:Json(name = "expires_in")
    val expiresIn: Int
)

@Entity(tableName = "token")
data class TokenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("access_token")
    val accessToken: String,
)