package com.ashtar.bus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ashtar.bus.component.ErrorDialog
import com.ashtar.bus.data.InitialRepository
import com.ashtar.bus.ui.AppNavHost
import com.ashtar.bus.ui.theme.BusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    var uiState by rememberSaveable { mutableStateOf(UiState.Loading) }

                    LaunchedEffect(Unit) {
                        uiState = try {
                            initialRepository.initial()
                            UiState.Start
                        } catch (e: Exception) {
                            UiState.Error
                        }
                    }
                    when(uiState) {
                        UiState.Loading,
                        UiState.Error -> {
                            InitialScreen(
                                uiState = uiState,
                                confirm = { uiState = UiState.Start }
                            )
                        }
                        UiState.Start -> {
                            AppNavHost()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialScreen(
    uiState: UiState,
    confirm: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (uiState == UiState.Error) {
            ErrorDialog(
                title = "連線失敗",
                content = "無法取得最新資料",
                closeDialog = {},
                confirm = confirm
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 5.dp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "資料同步中",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

enum class UiState {
    Loading,
    Error,
    Start
}