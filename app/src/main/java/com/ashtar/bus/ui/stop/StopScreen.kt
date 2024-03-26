@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.ashtar.bus.ui.stop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ashtar.bus.common.StopStatus
import com.ashtar.bus.component.BackIconButton
import com.ashtar.bus.component.GrayDivider
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.Route
import com.ashtar.bus.model.Stop
import com.ashtar.bus.model.StopOfRoute
import com.ashtar.bus.ui.theme.BusTheme
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

    val uiState by viewModel.uiState.collectAsState()

    ScreenContent(
        uiState = uiState,
        route = viewModel.route,
        stopOfRouteList = viewModel.stopOfRouteList,
        nextUpdateIn = viewModel.nextUpdateIn,
        dialog = viewModel.dialog,
        navigateUp = navigateUp,
        openMenuDialog = viewModel::openMenuDialog,
        openAddToGroupDialog = viewModel::openAddToGroupDialog,
        openNewGroupDialog = viewModel::openNewGroupDialog,
        closeDialog = viewModel::closeDialog,
        insertMarkedStop = viewModel::insertMarkedStop,
        insertGroupWithMarkedStop = viewModel::insertGroupWithMarkedStop
    )
}

@Composable
fun ScreenContent(
    uiState: UiState,
    route: Route,
    stopOfRouteList: List<StopOfRoute>,
    nextUpdateIn: Int,
    dialog: Dialog,
    navigateUp: () -> Unit = {},
    openMenuDialog: (Stop) -> Unit = {},
    openAddToGroupDialog: (Stop) -> Unit = {},
    openNewGroupDialog: (Stop) -> Unit = {},
    closeDialog: () -> Unit = {},
    insertMarkedStop: (Group, Stop) -> Unit = { _, _ -> },
    insertGroupWithMarkedStop: (String, Stop) -> Unit = { _, _ -> }
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState { stopOfRouteList.size }

        Column(Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                divider = {}
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
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 5.dp
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    LazyColumn {
                        itemsIndexed(
                            stopOfRouteList[page].stops,
                            key = { _, stop -> stop.id }
                        ) { index, stop ->
                            if (index > 0) {
                                GrayDivider()
                            }
                            StopItem(
                                stop = stop,
                                modifier = Modifier.clickable { openMenuDialog(stop) }
                            )
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

        when (dialog) {
            is Dialog.None -> {}
            is Dialog.Menu -> {
                MenuDialog(
                    stop = dialog.stop,
                    openAddToGroupDialog = { openAddToGroupDialog(dialog.stop) },
                    closeDialog = closeDialog
                )
            }
            is Dialog.AddToGroup -> {
                AddToGroupDialog(
                    stop = dialog.stop,
                    groupList = dialog.groupList,
                    openNewGroupDialog = { openNewGroupDialog(dialog.stop) },
                    closeDialog = closeDialog,
                    insertMarkedStop = insertMarkedStop
                )
            }
            is Dialog.NewGroup -> {
                NewGroupDialog(
                    stop = dialog.stop,
                    closeDialog = closeDialog,
                    insertGroupWithMarkedStop = insertGroupWithMarkedStop
                )
            }
        }
    }
}

@Composable
fun StopItem(
    stop: Stop,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(textAlign = TextAlign.Center)
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                stop.estimatedMin != null -> {
                    if (stop.estimatedMin > 0) {
                        Surface(
                            modifier = Modifier.size(84.dp, 36.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stop.estimatedMin.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium
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
                            color = Color(0xFFE62424),
                            contentColor = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "即將進站",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                else -> {
                    Surface(
                        modifier = Modifier.size(84.dp, 36.dp),
                        shape = MaterialTheme.shapes.small,
                        color = Color(0xFFEAEAEA),
                        contentColor = Color.DarkGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stop.stopStatus.display,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stop.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
            if (stop.plateNumbs.isEmpty()) {
                Box(
                    modifier = Modifier.width(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(12.dp)) {
                        drawCircle(color = Color(0xFFE3E3E3))
                    }
                }
            } else {
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = Color(0xFF51AF60),
                    contentColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                        stop.plateNumbs.forEach {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StopPreview() {
    BusTheme {
        ScreenContent(
            uiState = UiState(isLoading = false),
            route = Route(
                name = "307",
                departureStop = "板橋",
                destinationStop = "撫遠街"
            ),
            stopOfRouteList = listOf(
                StopOfRoute(
                    destination = "撫遠街",
                    stops = listOf(
                        Stop(
                            id = "1",
                            name = "板橋國中",
                            direction = 0,
                            stopStatus = StopStatus.NotDepart,
                            estimatedMin = null,
                            plateNumbs = emptyList()
                        ),
                        Stop(
                            id = "2",
                            name = "板橋國中",
                            direction = 0,
                            stopStatus = StopStatus.LastPassed,
                            estimatedMin = null,
                            plateNumbs = emptyList()
                        ),
                        Stop(
                            id = "3",
                            name = "板橋國中",
                            direction = 0,
                            stopStatus = StopStatus.Normal,
                            estimatedMin = 3,
                            plateNumbs = listOf("EAL-0072")
                        ),
                        Stop(
                            id = "4",
                            name = "板橋國中",
                            direction = 0,
                            stopStatus = StopStatus.Normal,
                            estimatedMin = 0,
                            plateNumbs = listOf("259-U5", "EAA-158")
                        ),
                    )
                ),
                StopOfRoute(
                    destination = "板橋",
                    stops = emptyList()
                )
            ),
            nextUpdateIn = 20,
            dialog = Dialog.None
        )
    }
}