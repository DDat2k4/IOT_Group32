package com.example.iot_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot_app.ui.auth.*
import com.example.iot_app.ui.device.DeviceViewModel
import com.example.iot_app.ui.main.DashboardScreen
import com.example.iot_app.ui.main.MainScreen
import com.example.iot_app.ui.user.UserViewModel

@Composable
fun AppNavGraph(viewModel: AuthViewModel, userViewModel: UserViewModel, deviceViewModel: DeviceViewModel) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = "login"
    ) {
        // MÀN HÌNH ĐĂNG KÝ
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                navController = nav,
                onRegisterSuccess = {
                    // Quay lại màn hình đăng nhập và dọn dẹp chính nó
                    nav.popBackStack()
                }
            )
        }

        // MÀN HÌNH ĐĂNG NHẬP
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                navController = nav,
                onLoginSuccess = {
                    nav.navigate("main") {
                        // Xóa màn hình login khỏi stack để không thể quay lại khi ấn back
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = {
                    nav.navigate("forgot")
                }
            )
        }

        // MÀN HÌNH CHÍNH (Sau khi đăng nhập)
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                userViewModel = userViewModel,
                deviceViewModel = deviceViewModel,
                onLogout = {
                    nav.navigate("login") {
                        userViewModel.clearData()
                        // Xóa sạch toàn bộ lịch sử khi đăng xuất
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // MÀN HÌNH QUÊN MẬT KHẨU (NHẬP EMAIL)
        composable("forgot") {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onOtpSent = { email ->
                    nav.navigate("reset/$email")
                },
                onBack = { nav.popBackStack() }
            )
        }

        // MÀN HÌNH ĐẶT LẠI MẬT KHẨU (NHẬP OTP & PASS MỚI)
        composable("reset/{email}") { backStack ->
            val email = backStack.arguments?.getString("email") ?: ""

            ResetPasswordScreen(
                viewModel = viewModel,
                email = email,
                onDone = {
                    // Đổi xong đưa về login và xóa các màn hình reset/forgot trung gian
                    nav.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onCancel = {
                    nav.popBackStack("login", inclusive = false)
                }
            )
        }

        composable("dashboard") {
            DashboardScreen()
        }
    }
}