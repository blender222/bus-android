package com.ashtar.bus.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashtar.bus.common.City
import com.ashtar.bus.model.Route

@Composable
fun SearchScreen(
    navigateUp: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {

    ScreenContent(
        query = viewModel.query,
        routeList = viewModel.routeList,
        navigateUp = navigateUp,
        onSearchInput = viewModel::onSearchInput
    )
}

@Composable
fun ScreenContent(
    query: String,
    routeList: List<Route>,
    navigateUp: () -> Unit,
    onSearchInput: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                query = query,
                navigateUp = navigateUp,
                onSearchInput = onSearchInput
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(top = 4.dp, start = 8.dp, end = 8.dp)
        ) {
            itemsIndexed(routeList) { index, route ->
                if (index > 0) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color(0xFFEEEEEE)
                    )
                }
                RouteCard(route)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    query: String,
    navigateUp: () -> Unit,
    onSearchInput: (String) -> Unit
) {
    TopAppBar(
        title = {
            BasicTextField(
                value = query,
                onValueChange = onSearchInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, MaterialTheme.shapes.medium),
                textStyle = MaterialTheme.typography.bodyLarge,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "搜尋公車路線",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onSearchInput("") }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "清除",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    Icons.Outlined.ArrowBackIos,
                    contentDescription = "返回"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray
        )
    )
}

@Composable
fun RouteCard(route: Route) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(start = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = route.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${route.departureStop} - ${route.destinationStop}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.BookmarkAdd,
                    contentDescription = "加入常用路線",
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = route.city.zhName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun NamePreview() {
    val routeList = List(20) {
        Route(
            name = "307",
            city = City.Taipei,
            departureStop = "板橋",
            destinationStop = "撫遠街"
        )
    }
    ScreenContent(
        query = "307",
        routeList = routeList,
        navigateUp = {},
        onSearchInput = {}
    )
}