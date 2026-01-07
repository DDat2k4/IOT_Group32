package com.example.iot_app.ui.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iot_app.ui.alert.AlertScreen
import com.example.iot_app.ui.alert.AlertViewModel
import com.example.iot_app.ui.dashboard.DashboardScreen
import com.example.iot_app.ui.dashboard.DashboardViewModel
import com.example.iot_app.ui.dashboard.RoomDetailScreen
import com.example.iot_app.ui.device.DeviceManagerScreen
import com.example.iot_app.ui.device.DeviceViewModel
import com.example.iot_app.ui.home.HomeScreen
import com.example.iot_app.ui.user.ProfileScreen
import com.example.iot_app.ui.user.ChangePasswordScreen
import com.example.iot_app.ui.user.UserViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun MainNavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    deviceViewModel: DeviceViewModel,
    alertViewModel: AlertViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(viewModel = userViewModel)
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardViewModel,
                navController = navController
            )
        }

        composable(
            route = "room_detail/{deviceCode}",
            arguments = listOf(navArgument("deviceCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceCode = backStackEntry.arguments?.getString("deviceCode") ?: ""
            RoomDetailScreen(
                deviceCode = deviceCode,
                viewModel = dashboardViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("devices") {
            DeviceManagerScreen(viewModel = deviceViewModel)
        }
        composable("alerts") {
            AlertScreen(viewModel = alertViewModel)
        }

        composable("profile") {
            ProfileScreen(viewModel = userViewModel)
        }
        composable("change_password") {
            ChangePasswordScreen(viewModel = userViewModel)
        }
    }
}

