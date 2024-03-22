@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.ashtar.bus.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashtar.bus.common.City
import com.ashtar.bus.component.GrayDivider
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.MarkedStop
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    toRoute: () -> Unit,
    toGroupManage: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ScreenContent(
        uiState = uiState,
        toRoute = toRoute,
        toGroupManage = toGroupManage
    )
}

@Composable
fun ScreenContent(
    uiState: UiState,
    toRoute: () -> Unit,
    toGroupManage: () -> Unit
) {
    Scaffold(
        topBar = { HomeTopBar(toRoute, toGroupManage) }
    ) { padding ->
        when(uiState) {
            is UiState.Loading -> {
                Column(modifier = Modifier.padding(padding)) {
                    EmptyTabRow()
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
            is UiState.Success -> {
                val groupList = uiState.groupList
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState { groupList.size }

                Column(modifier = Modifier.padding(padding)) {
                    if (groupList.isEmpty()) {
                        EmptyTabRow()
                        GroupPlaceholder(toRoute)
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
                                    },
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text(
                                        text = group.name,
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
                                GroupPlaceholder(toRoute)
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    itemsIndexed(list) { index, item: MarkedStop ->
                                        if (index > 0) {
                                            GrayDivider()
                                        }
                                        MarkedStopItem(item)
                                    }
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
fun HomeTopBar(
    toRoute: () -> Unit,
    toGroupManage: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
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
                    offset = DpOffset(Int.MIN_VALUE.dp, Int.MIN_VALUE.dp)
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
                                text = "編輯群組站牌",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = { /*TODO*/ }
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
}

@Composable
fun MarkedStopItem(item: MarkedStop) {
    Row(
        modifier = Modifier
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(84.dp, 36.dp),
            shape = MaterialTheme.shapes.small,
            color = Color.Gray,
            contentColor = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "尚未發車",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.routeName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "往${item.destination}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.city.zhName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = item.stopName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "10秒後更新",
                    modifier = Modifier.align(Alignment.Bottom),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun EmptyTabRow() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    )
}

@Composable
fun GroupPlaceholder(toRoute: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.LibraryBooks,
            contentDescription = null,
            modifier = Modifier.size(108.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "搜尋路線，加入常用站牌",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = toRoute,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "前往搜尋",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    ScreenContent(
        // uiState = UiState.Loading,
        uiState = UiState.Success(
            groupList = listOf(
                Group(id = 1, name = "上班") to listOf(
                    MarkedStop(
                        routeName = "307",
                        destination = "撫遠街",
                        city = City.Taipei,
                        stopName = "西藏路口"
                    ),
                    MarkedStop(
                        routeName = "307",
                        destination = "板橋",
                        city = City.Taipei,
                        stopName = "萬大路"
                    )
                ),
                Group(id = 2, name = "下班") to emptyList(),
                Group(id = 3, name = "出去玩") to emptyList(),
            )
        ),
        toRoute = {},
        toGroupManage = {}
    )
}