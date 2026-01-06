package com.example.iot_app.ui.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iot_app.ui.device.DeviceManagerScreen
import com.example.iot_app.ui.device.DeviceViewModel
import com.example.iot_app.ui.home.HomeScreen
import com.example.iot_app.ui.user.ProfileScreen
import com.example.iot_app.ui.user.ChangePasswordScreen
import com.example.iot_app.ui.user.UserViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    deviceViewModel: DeviceViewModel,
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
        composable("dashboard") { DashboardScreen() }
        composable("devices") {
            DeviceManagerScreen(viewModel = deviceViewModel)
        }
        composable("alerts") { Text("Alerts") }

        composable("profile") {
            ProfileScreen(viewModel = userViewModel)
        }
        composable("change_password") {
            ChangePasswordScreen(viewModel = userViewModel)
        }
    }
}
