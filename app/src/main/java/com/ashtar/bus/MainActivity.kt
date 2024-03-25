package com.ashtar.bus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ashtar.bus.common.SessionManager
import com.ashtar.bus.component.HomeLoading
import com.ashtar.bus.data.InitialRepository
import com.ashtar.bus.ui.AppNavHost
import com.ashtar.bus.ui.theme.BusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var initialRepository: InitialRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoading by rememberSaveable { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        sessionManager.initToken()
                        initialRepository.refreshRoute()
                        isLoading = false
                    }
                    if (isLoading) {
                        HomeLoading()
                    } else {
                        AppNavHost()
                    }
                }
            }
        }
    }
}