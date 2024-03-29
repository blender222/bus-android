package com.ashtar.bus.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ashtar.bus.ui.group_manage.GroupManageScreen
import com.ashtar.bus.ui.home.HomeScreen
import com.ashtar.bus.ui.marked_stop_manage.MarkedStopManageScreen
import com.ashtar.bus.ui.route.RouteScreen
import com.ashtar.bus.ui.stop.StopScreen

enum class Destination {
    Home,
    Route,
    Stop,
    GroupManage,
    MarkedStopManage
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.name
    ) {
        composable(Destination.Home.name) {
            HomeScreen(
                toRoute = {
                    navController.navigate(Destination.Route.name)
                },
                toStop = { routeId ->
                    navController.navigate("${Destination.Stop.name}/$routeId")
                },
                toGroupManage = {
                    navController.navigate(Destination.GroupManage.name)
                },
                toMarkedStopManage = { groupId ->
                    navController.navigate("${Destination.MarkedStopManage.name}/$groupId")
                }
            )
        }
        composable(Destination.Route.name) {
            RouteScreen(
                navigateUp = { navController.navigateUp() },
                toStop = { routeId ->
                    navController.navigate("${Destination.Stop.name}/$routeId")
                }
            )
        }
        composable("${Destination.Stop.name}/{routeId}", arguments = listOf(
            navArgument("routeId") { type = NavType.StringType }
        )) {
            StopScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
        composable(Destination.GroupManage.name) {
            GroupManageScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
        composable("${Destination.MarkedStopManage.name}/{groupId}", arguments = listOf(
            navArgument("groupId") { type = NavType.IntType }
        )) {
            MarkedStopManageScreen(
                navigateUp = { navController.navigateUp() },
                toRoute = {
                    navController.navigate(Destination.Route.name) {
                        popUpTo(Destination.Home.name)
                    }
                }
            )
        }
    }
}