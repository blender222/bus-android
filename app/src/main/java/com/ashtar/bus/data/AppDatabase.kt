package com.ashtar.bus.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.dao.TokenDao
import com.ashtar.bus.model.RouteEntity
import com.ashtar.bus.model.TokenEntity

@Database(
    entities = [TokenEntity::class, RouteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun routeDao(): RouteDao
}