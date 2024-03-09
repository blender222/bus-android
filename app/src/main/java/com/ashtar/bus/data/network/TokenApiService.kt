package com.ashtar.bus.data.network

import com.ashtar.bus.data.model.Token
import retrofit2.http.GET

interface TokenApiService {
    @GET("token")
    suspend fun getToken(): Token
}