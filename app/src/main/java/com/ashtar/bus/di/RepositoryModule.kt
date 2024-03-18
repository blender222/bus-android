package com.ashtar.bus.di

import com.ashtar.bus.data.BusRepository
import com.ashtar.bus.data.BusRepositoryImpl
import com.ashtar.bus.data.StopRepository
import com.ashtar.bus.data.StopRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindBusRepository(impl: BusRepositoryImpl): BusRepository

    @Binds
    abstract fun bindStopRepository(impl: StopRepositoryImpl): StopRepository
}