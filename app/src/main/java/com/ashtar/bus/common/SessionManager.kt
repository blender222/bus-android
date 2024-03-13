package com.ashtar.bus.common

import android.util.Log
import com.ashtar.bus.data.dao.TokenDao
import com.ashtar.bus.data.network.TokenApiService
import com.ashtar.bus.model.TokenEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val tokenApiService: TokenApiService,
    private val tokenDao: TokenDao
) {
    lateinit var accessToken: String

    suspend fun initToken() {
        try {
            val storedToken = tokenDao.getToken()
            if (storedToken.isNotEmpty()) {
                accessToken = storedToken[0]
                return
            }
            refreshToken()
        } catch (e: Exception) {
            Log.e("init token", null, e)
        }
    }

    suspend fun refreshToken() {
        try {
            val response = tokenApiService.getToken()
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenDao.upsert(TokenEntity(1, body.accessToken))
                accessToken = body.accessToken
            }
        } catch (e: Exception) {
            Log.e("refresh token", null, e)
        }
    }
}