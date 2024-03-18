@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.ashtar.bus.ui.stop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ashtar.bus.common.StopStatus
import com.ashtar.bus.component.BackIconButton
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.Stop
import com.ashtar.bus.model.StopOfRoute
import kotlinx.coroutines.launch

@Composable
fun StopScreen(
    navigateUp: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: StopViewModel = hiltViewModel()
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.startRefreshJob()
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.stopRefreshJob()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ScreenContent(
        state = viewModel.state,
        route = viewModel.route,
        stopOfRouteList = viewModel.stopOfRouteList,
        nextUpdateIn = viewModel.nextUpdateIn,
        navigateUp = navigateUp
    )
}

@Composable
fun ScreenContent(
    state: UiState,
    route: Route,
    stopOfRouteList: List<StopOfRoute>,
    nextUpdateIn: Int,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = route.name,
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = { BackIconButton(navigateUp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray
                )
            )
        }
    ) { padding ->
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState { stopOfRouteList.size }

        Column(Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                stopOfRouteList.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    ) {
                        Text(
                            text = "往${item.destination}",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            if (state == UiState.Loading) {
                Text("載入中")
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(stopOfRouteList[page].stops) { index, stop ->
                            if (index > 0) {
                                GrayDivider()
                            }
                            StopCard(stop)
                        }
                    }
                }
            }
            Surface(color = MaterialTheme.colorScheme.primary) {
                Text(
                    text = if (nextUpdateIn < REFRESH_INTERVAL) {
                        "$nextUpdateIn 秒後更新"
                    } else {
                        "到站時間已更新"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun StopCard(stop: Stop) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                stop.estimatedMin != null -> {
                    if (stop.estimatedMin > 0) {
                        Surface(
                            modifier = Modifier.size(84.dp, 36.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stop.estimatedMin.toString(),
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "分",
                                    modifier = Modifier.padding(top = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.size(84.dp, 36.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("即將進站")
                            }
                        }
                    }
                }
                else -> {
                    Surface(
                        modifier = Modifier.size(84.dp, 36.dp),
                        shape = MaterialTheme.shapes.small,
                        color = Color.Gray,
                        contentColor = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(stop.stopStatus.display)
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stop.name
            )
        }
    }
}

@Composable
fun GrayDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = Color(0xFFEEEEEE)
    )
}

@Preview
@Composable
fun StopScreenPreview() {
    ScreenContent(
        state = UiState.Started,
        route = Route(
            name = "307",
            departureStop = "板橋",
            destinationStop = "撫遠街"
        ),
        stopOfRouteList = listOf(
            StopOfRoute(
                destination = "撫遠街",
                stops = listOf(
                    Stop(id = "", name = "板橋國中", stopStatus = StopStatus.NotDepart, estimatedMin = null),
                    Stop(id = "", name = "板橋國中", stopStatus = StopStatus.LastPassed, estimatedMin = null),
                    Stop(id = "", name = "板橋國中", stopStatus = StopStatus.Normal, estimatedMin = 3),
                    Stop(id = "", name = "板橋國中", stopStatus = StopStatus.Normal, estimatedMin = 0),
                )
            ),
            StopOfRoute(
                destination = "板橋",
                stops = emptyList()
            )
        ),
        nextUpdateIn = 20,
        navigateUp = {}
    )
}