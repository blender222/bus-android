package com.ashtar.bus.di

import com.ashtar.bus.data.BusRepository
import com.ashtar.bus.data.BusRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindBusRepository(impl: BusRepositoryImpl): BusRepository
}