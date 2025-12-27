package com.example.iot_app.ui.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iot_app.ui.home.HomeScreen
import com.example.iot_app.ui.user.ProfileScreen
import com.example.iot_app.ui.user.ChangePasswordScreen
import com.example.iot_app.ui.user.UserViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel, // THÊM THAM SỐ NÀY
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen() }
        composable("dashboard") { DashboardScreen() }
        composable("devices") { Text("Devices") }
        composable("alerts") { Text("Alerts") }

        // TRUYỀN VIEWMODEL VÀO ĐÂY
        composable("profile") {
            ProfileScreen(viewModel = userViewModel)
        }
        composable("change_password") {
            ChangePasswordScreen(viewModel = userViewModel)
        }
    }
}
