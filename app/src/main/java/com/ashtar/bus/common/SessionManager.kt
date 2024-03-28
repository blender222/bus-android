package com.ashtar.bus.common

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
        val storedToken = tokenDao.getToken()
        if (storedToken.isNotEmpty()) {
            accessToken = storedToken[0]
            return
        }
        refreshToken()
    }

    suspend fun refreshToken() {
        val response = tokenApiService.getToken()
        if (response.isSuccessful) {
            val body = response.body()!!
            tokenDao.upsert(TokenEntity(1, body.accessToken))
            accessToken = body.accessToken
        }
    }
}