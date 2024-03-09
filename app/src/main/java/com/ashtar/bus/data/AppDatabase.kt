package com.ashtar.bus.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashtar.bus.data.dao.TokenDao
import com.ashtar.bus.data.model.TokenEntity

@Database(
    entities = [TokenEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
}