package com.ashtar.bus.common

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BusApiInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${sessionManager.accessToken}")
            .build()
        var response = chain.proceed(request)

        var retry = 0
        while (!response.isSuccessful && response.code() != 304 && retry < 3) {
            retry++
            if (response.code() == 401) {
                runBlocking {
                    sessionManager.refreshToken()
                }
                request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${sessionManager.accessToken}")
                    .build()
            }
            response.close()
            response = chain.proceed(request)
        }
        return response
    }
}