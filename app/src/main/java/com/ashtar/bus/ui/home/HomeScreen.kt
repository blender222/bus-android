@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.ashtar.bus.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ashtar.bus.common.DataProvider
import com.ashtar.bus.component.GrayDivider
import com.ashtar.bus.component.HomeLoading
import com.ashtar.bus.component.MarkedStopText
import com.ashtar.bus.component.SearchPlaceholder
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.MarkedStop
import com.ashtar.bus.ui.theme.BusTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    toRoute: () -> Unit,
    toStop: (String) -> Unit,
    toGroupManage: () -> Unit,
    toMarkedStopManage: (Int) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: HomeViewModel = hiltViewModel()
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

    val groupList by viewModel.groupList.collectAsState()

    ScreenContent(
        groupList = groupList,
        nextUpdateIn = viewModel.nextUpdateIn,
        toRoute = toRoute,
        toStop = toStop,
        toGroupManage = toGroupManage,
        toMarkedStopManage = toMarkedStopManage
    )
}

@Composable
fun ScreenContent(
    groupList: List<Pair<Group, List<MarkedStop>>>?,
    nextUpdateIn: Int,
    toRoute: () -> Unit = {},
    toStop: (String) -> Unit = {},
    toGroupManage: () -> Unit = {},
    toMarkedStopManage: (Int) -> Unit = {}
) {
    if (groupList == null) {
        HomeLoading()
    } else {
        var menuExpanded by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val pagerState = key(groupList) { rememberPagerState { groupList.size } }
        val currentGroup = groupList.getOrNull(pagerState.currentPage)?.first

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("上班族等公車") },
                    actions = {
                        IconButton(onClick = toRoute) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "前往搜尋公車路線"
                            )
                        }
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "更多選項"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            offset = DpOffset(Int.MIN_VALUE.dp, Int.MAX_VALUE.dp)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "管理群組",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    toGroupManage()
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "管理此群組站牌",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    toMarkedStopManage(currentGroup!!.id)
                                    menuExpanded = false
                                },
                                enabled = currentGroup != null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (groupList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                    SearchPlaceholder(toRoute)
                } else {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        groupList.forEachIndexed { index, (group, _) ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            ) {
                                Text(
                                    text = group.name,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        val list: List<MarkedStop> = groupList[page].second
                        if (list.isEmpty()) {
                            SearchPlaceholder(toRoute)
                        } else {
                            LazyColumn {
                                itemsIndexed(
                                    items = list,
                                    key = { _, it -> it.id }
                                ) { index, item: MarkedStop ->
                                    if (index > 0) {
                                        GrayDivider(Modifier.padding(horizontal = 16.dp))
                                    }
                                    MarkedStopItem(
                                        item = item,
                                        nextUpdateIn = nextUpdateIn,
                                        onClick = { toStop(item.routeId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarkedStopItem(
    item: MarkedStop,
    nextUpdateIn: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            item.estimatedMin != null -> {
                if (item.estimatedMin > 0) {
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
                                text = item.estimatedMin.toString(),
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
                            text = item.stopStatus?.display ?: "讀取中",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        MarkedStopText(
            item = item,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.city.zhName,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (nextUpdateIn < REFRESH_INTERVAL) {
                    "$nextUpdateIn 秒後更新"
                } else {
                    "已更新"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    BusTheme {
        ScreenContent(
            // groupList = null,
            groupList = listOf(
                Group(id = 1, sort = 1, name = "上班") to DataProvider.markedStopList,
                Group(id = 2, sort = 2, name = "下班") to emptyList(),
                Group(id = 3, sort = 3, name = "出去玩") to emptyList(),
            ),
            nextUpdateIn = REFRESH_INTERVAL,
            toRoute = {},
            toGroupManage = {}
        )
    }
}