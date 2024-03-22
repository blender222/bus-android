package com.ashtar.bus.di

import com.ashtar.bus.data.RouteRepository
import com.ashtar.bus.data.RouteRepositoryImpl
import com.ashtar.bus.data.GroupRepository
import com.ashtar.bus.data.GroupRepositoryImpl
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
    @Singleton
    @Binds
    abstract fun bindRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Singleton
    @Binds
    abstract fun bindStopRepository(impl: StopRepositoryImpl): StopRepository

    @Singleton
    @Binds
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository
}