package com.ashtar.bus.di

import com.ashtar.bus.common.SessionManager
import com.ashtar.bus.data.network.BusApiService
import com.ashtar.bus.data.network.TokenApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Provides
    fun provideTokenApiService(): TokenApiService {
        return Retrofit.Builder()
            .baseUrl("https://bus.eventnow.top/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TokenApiService::class.java)
    }

    @Provides
    fun provideBusApiService(sessionManager: SessionManager): BusApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${sessionManager.accessToken}")
                    .build()
                chain.proceed(request)
            }
            .build()
        return Retrofit.Builder()
            .baseUrl("https://tdx.transportdata.tw/api/basic/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BusApiService::class.java)
    }
}