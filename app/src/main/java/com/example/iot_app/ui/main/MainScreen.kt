package com.example.iot_app.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.iot_app.ui.auth.AuthViewModel
import com.example.iot_app.ui.user.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val nav = rememberNavController() // Biến điều hướng chính

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent { route ->
                nav.navigate(route) {
                    // Dọn dẹp stack để quay về trang home làm gốc
                    popUpTo(nav.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                scope.launch { drawerState.close() }
            }
        }
    ) {
        Scaffold(
            topBar = {
                MainTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfile = {
                        nav.navigate("profile") {
                            // popUpTo sử dụng ID của màn hình khởi đầu để tránh lỗi đỏ
                            popUpTo(nav.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onChangePassword = {
                        nav.navigate("change_password") {
                            popUpTo(nav.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = { viewModel.logout { onLogout() } }
                )
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                MainNavGraph(
                    navController = nav,
                    userViewModel = userViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}