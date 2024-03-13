package com.ashtar.bus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ashtar.bus.ui.home.HomeScreen
import com.ashtar.bus.ui.search.SearchScreen

enum class Destination {
    Home,
    Search
}

@Composable
fun BusNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.name
    ) {
        composable(Destination.Home.name) {
            HomeScreen(
                toSearch = { navController.navigate(Destination.Search.name) }
            )
        }
        composable(Destination.Search.name) {
            SearchScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
    }
}