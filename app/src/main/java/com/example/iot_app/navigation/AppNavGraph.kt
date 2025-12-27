package com.example.iot_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot_app.ui.auth.AuthViewModel
import com.example.iot_app.ui.auth.LoginScreen
import com.example.iot_app.ui.auth.ForgotPasswordScreen
import com.example.iot_app.ui.auth.ResetPasswordScreen
import com.example.iot_app.ui.dashboard.DashboardScreen

@Composable
fun AppNavGraph(viewModel: AuthViewModel) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    nav.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = {
                    nav.navigate("forgot")
                }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onOtpSent = {
                    nav.navigate("reset")
                }
            )
        }

        composable("reset") {
            ResetPasswordScreen(
                viewModel = viewModel,
                onDone = {
                    nav.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen()
        }
    }
}
