package com.ashtar.bus.di

import android.content.Context
import androidx.room.Room
import com.ashtar.bus.data.AppDatabase
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.dao.TokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
    }

    @Provides
    fun provideTokenDao(database: AppDatabase): TokenDao {
        return database.tokenDao()
    }

    @Provides
    fun provideRouteDao(database: AppDatabase): RouteDao {
        return database.routeDao()
    }
}