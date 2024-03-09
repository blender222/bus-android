package com.ashtar.bus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ashtar.bus.ui.home.HomeScreen

enum class Destination {
    Home
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
            HomeScreen()
        }
    }
}