package com.ashtar.bus.data.network

import com.ashtar.bus.model.TokenJson
import retrofit2.Response
import retrofit2.http.GET

interface TokenApiService {
    @GET("token")
    suspend fun getToken(): Response<TokenJson>
}