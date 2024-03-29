package com.ashtar.bus.ui.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashtar.bus.common.City
import com.ashtar.bus.component.BackIconButton
import com.ashtar.bus.component.GrayDivider
import com.ashtar.bus.model.Route
import com.ashtar.bus.ui.theme.BusTheme
import kotlinx.coroutines.flow.filter

@Composable
fun RouteScreen(
    navigateUp: () -> Unit,
    toStop: (String) -> Unit,
    viewModel: RouteViewModel = hiltViewModel()
) {
    ScreenContent(
        state = viewModel.state,
        query = viewModel.query,
        searchedList = viewModel.searchedList,
        markedList = viewModel.markedList,
        navigateUp = navigateUp,
        toStop = toStop,
        getByKeyboard = viewModel::getByKeyboard,
        getByReplace = viewModel::getByReplace,
        getByInput = viewModel::getByInput,
        toggleMarked = viewModel::toggleMarked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    state: SearchState,
    query: TextFieldValue,
    searchedList: List<Route>,
    markedList: List<Route>,
    navigateUp: () -> Unit,
    toStop: (String) -> Unit,
    getByKeyboard: (TextFieldValue) -> Unit,
    getByReplace: (String) -> Unit,
    getByInput: (String) -> Unit,
    toggleMarked: (Route) -> Unit
) {
    var showKeyboard by remember { mutableStateOf(true) }
    var keyboardMore by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    if (isPressed) {
        showKeyboard = true
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    CompositionLocalProvider(LocalTextInputService provides null) {
                        BasicTextField(
                            value = query,
                            onValueChange = getByKeyboard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .focusRequester(focusRequester)
                                .background(Color.White, MaterialTheme.shapes.medium),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            interactionSource = interactionSource,
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
                                        if (query.text.isEmpty()) {
                                            Text(
                                                text = "搜尋公車路線",
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                        innerTextField()
                                    }
                                    if (query.text.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                getByReplace("")
                                                showKeyboard = true
                                            }
                                        ) {
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
                    }
                },
                navigationIcon = { BackIconButton(navigateUp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                searchedList.isNotEmpty() -> {
                    RouteLazyList(hideKeyboard = { showKeyboard = false }) {
                        itemsIndexed(
                            items = searchedList,
                            key = { _, it -> it.id }
                        ) { index, route ->
                            if (index > 0) {
                                GrayDivider(Modifier.padding(horizontal = 16.dp))
                            }
                            RouteItem(route, toStop, toggleMarked)
                        }
                    }
                }
                state == SearchState.NoResult -> {
                    RouteLazyList(hideKeyboard = { showKeyboard = false }) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(32.dp))
                                Icon(
                                    Icons.Filled.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(108.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "沒有符合條件的路線",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                markedList.isNotEmpty() -> {
                    RouteLazyList(hideKeyboard = { showKeyboard = false }) {
                        itemsIndexed(
                            items = markedList,
                            key = { _, it -> it.id }
                        ) { index, route ->
                            if (index > 0) {
                                GrayDivider(Modifier.padding(horizontal = 16.dp))
                            }
                            RouteItem(route, toStop, toggleMarked)
                        }
                    }
                }
                else -> {
                    RouteLazyList(hideKeyboard = { showKeyboard = false }) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(32.dp))
                                Icon(
                                    Icons.Outlined.BookmarkAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(108.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "按下書籤加入常用路線!",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
            if (showKeyboard) {
                SearchKeyboard(
                    keyboardMore = keyboardMore,
                    setKeyboardMore = { keyboardMore = it },
                    getByReplace = getByReplace,
                    getByInput = getByInput,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun RouteLazyList(
    hideKeyboard: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { isScroll -> isScroll }
            .collect { hideKeyboard() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        content = content
    )
}

@Composable
fun RouteItem(
    route: Route,
    toStop: (String) -> Unit,
    toggleMarked: (Route) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { toStop(route.id) }
            .padding(start = 16.dp, end = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = route.name,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
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
            IconButton(onClick = { toggleMarked(route) }) {
                if (route.marked) {
                    Icon(
                        Icons.Filled.Bookmark,
                        contentDescription = "從常用路線中移除",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Outlined.BookmarkAdd,
                        contentDescription = "加入常用路線",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                text = route.city.zhName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        }
    }
}

@Preview
@Composable
fun RoutePreview() {
    val list = List(20) {
        Route(
            id = "",
            name = "307",
            city = City.Taipei,
            departureStop = "板橋",
            destinationStop = "撫遠街",
            marked = it % 2 == 0
        )
    }
    BusTheme {
        ScreenContent(
            state = SearchState.Initial,
            query = TextFieldValue("307"),
            searchedList = list,
            markedList = emptyList(),
            navigateUp = {},
            toStop = {},
            getByKeyboard = {},
            getByReplace = {},
            getByInput = {},
            toggleMarked = {}
        )
    }
}