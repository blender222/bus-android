package com.ashtar.bus.common

import android.util.Log
import com.ashtar.bus.data.dao.TokenDao
import com.ashtar.bus.data.model.TokenEntity
import com.ashtar.bus.data.network.TokenApiService
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
            accessToken = if (storedToken.isEmpty()) {
                val token = tokenApiService.getToken()
                tokenDao.insert(TokenEntity(1, token.accessToken))
                token.accessToken
            } else {
                storedToken[0]
            }
        } catch (e: Exception) {
            Log.e(this::class.simpleName, null, e)
        }
    }

    suspend fun refreshToken() {
        try {
            val token = tokenApiService.getToken()
            tokenDao.update(TokenEntity(1, token.accessToken))
            accessToken = token.accessToken
        } catch (e: Exception) {
            Log.e(this::class.simpleName, null, e)
        }
    }
}