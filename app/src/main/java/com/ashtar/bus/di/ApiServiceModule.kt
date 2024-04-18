package com.ashtar.bus.di

import com.ashtar.bus.common.BusApiInterceptor
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.data.network.TokenApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Qualifier
    annotation class TokenRetrofit

    @Qualifier
    annotation class TdxRetrofit

    @TokenRetrofit
    @Singleton
    @Provides
    fun provideTokenRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bus.app-event.site/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @TdxRetrofit
    @Singleton
    @Provides
    fun provideTdxRetrofit(busApiInterceptor: BusApiInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(busApiInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://tdx.transportdata.tw/api/basic/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideTokenApiService(@TokenRetrofit retrofit: Retrofit): TokenApiService {
        return retrofit.create(TokenApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideBusApiService(@TdxRetrofit retrofit: Retrofit): BusApiService {
        return retrofit.create(BusApiService::class.java)
    }
}