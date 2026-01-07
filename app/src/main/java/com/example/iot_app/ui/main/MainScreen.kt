package com.example.iot_app.ui.main

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.api.RetrofitClient
import com.example.iot_app.data.remote.repository.AlertRepository
import com.example.iot_app.data.remote.repository.DeviceRepository
import com.example.iot_app.data.remote.websocket.WebSocketService
import com.example.iot_app.ui.MainViewModel
import com.example.iot_app.ui.alert.AlertViewModel
import com.example.iot_app.ui.auth.AuthViewModel
import com.example.iot_app.ui.dashboard.DashboardViewModel
import com.example.iot_app.ui.device.DeviceViewModel
import com.example.iot_app.ui.user.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: AuthViewModel,
    userViewModel: UserViewModel,
    deviceViewModel: DeviceViewModel,
    alertViewModel: AlertViewModel,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val nav = rememberNavController()
    val context = LocalContext.current

    // Khởi tạo TokenManager và Repository
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { RetrofitClient.getInstance(context) }
    val deviceRepository = remember { DeviceRepository(apiService) }
    val alertRepository = remember { AlertRepository(apiService) }

    // MainViewModel quản lý logic chấm đỏ Dashboard
    val mainViewModel: MainViewModel = viewModel()

    // DashboardViewModel
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(deviceRepository, alertRepository) as T
            }
        }
    )

    // Lấy trạng thái chấm đỏ từ Dashboard
    val showDashBadge by mainViewModel.showDashboardBadge.collectAsState()

    // TopBar sẽ đỏ nếu Dashboard đang báo đỏ
    val menuHasNotification = showDashBadge

    // Lắng nghe dữ liệu phòng từ Dashboard
    val rooms by dashboardViewModel.rooms.collectAsState()

    //tải dữ liệu Dashboard ngay
    // tính toán chấm đỏ ngay khi vào màn hình chính mà không cần chờ bấm vào tab Dashboard
    LaunchedEffect(Unit) {
        dashboardViewModel.startAutoRefresh()
    }

    // Khi danh sách phòng thay đổi cập nhật trạng thái chấm đỏ cho MainViewModel
    LaunchedEffect(rooms) {
        mainViewModel.updateDashboardBadge(rooms)
    }

    // Tải Profile User
    val userProfile by userViewModel.profile.collectAsState()
    LaunchedEffect(Unit) {
        if (userProfile == null) {
            userViewModel.loadProfile()
        }
    }

    // khởi chạy service, tab ra ngoài k bị ngắt kn ws
    LaunchedEffect(userProfile) {
        userProfile?.let { user ->
            // Lấy Token thực tế từ TokenManager
            val token = tokenManager.getToken()

            if (token != null) {
                val intent = Intent(context, WebSocketService::class.java).apply {
                    putExtra("TOKEN", token)
                    putExtra("USER_ID", user.id)
                }

                // Bắt đầu Foreground Service để chạy ngầm bền vững
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }

                // AlertViewModel chỉ cần tải API lịch sử
                alertViewModel.connectSocketWithUser(user.id)
            }
        }
    }

    // Hàm xử lý Đăng xuất
    val handleLogout = {
        // Tắt Service khi đăng xuất để không nhận thông báo nữa
        val intent = Intent(context, WebSocketService::class.java)
        context.stopService(intent)

        userViewModel.clearData()
        viewModel.logout { onLogout() }
    }

    // gd
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                showDashboardBadge = showDashBadge
            ) { route ->
                nav.navigate(route) {
                    popUpTo(nav.graph.startDestinationId) { saveState = true }
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
                    hasNotification = menuHasNotification,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfile = {
                        nav.navigate("profile") {
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
                    onLogout = { handleLogout() }
                )
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                MainNavGraph(
                    navController = nav,
                    userViewModel = userViewModel,
                    deviceViewModel = deviceViewModel,
                    alertViewModel = alertViewModel,
                    dashboardViewModel = dashboardViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}