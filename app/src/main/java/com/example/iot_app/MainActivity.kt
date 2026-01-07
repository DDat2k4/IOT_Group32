package com.example.iot_app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.api.RetrofitClient
import com.example.iot_app.data.remote.repository.AlertRepository
import com.example.iot_app.data.remote.repository.AuthRepository
import com.example.iot_app.data.remote.repository.DeviceRepository
import com.example.iot_app.data.remote.repository.UserRepository
import com.example.iot_app.navigation.AppNavGraph
import com.example.iot_app.ui.alert.AlertViewModel
import com.example.iot_app.ui.auth.AuthViewModel
import com.example.iot_app.ui.device.DeviceViewModel
import com.example.iot_app.ui.user.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Kiểm tra Token và Quyền thông báo
        val tokenManager = TokenManager(applicationContext)
        val savedToken = tokenManager.getToken()

        // Xác định màn hình bắt đầu login/main
        val startDestination = if (!savedToken.isNullOrEmpty()) "main" else "login"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Khởi tạo API, Repository
        val api = RetrofitClient.getInstance(applicationContext)
        val authRepo = AuthRepository(api, tokenManager)
        val userRepo = UserRepository(api)
        val deviceRepo = DeviceRepository(api)
        val alertRepo = AlertRepository(api)

        setContent {
            // Factory cho AuthViewModel
            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { AuthViewModel(authRepo) }
                }
            )

            // Factory cho UserViewModel
            val userViewModel: UserViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        UserViewModel(userRepo, tokenManager)
                    }
                }
            )

            // Factory cho DeviceViewModel
            val deviceViewModel: DeviceViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { DeviceViewModel(deviceRepo) }
                }
            )

            // Factory cho AlertViewModel
            val alertViewModel: AlertViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AlertViewModel(
                            repository = alertRepo,
                           // tokenManager = tokenManager,
                            context = applicationContext
                        )
                    }
                }
            )

            MaterialTheme {
                AppNavGraph(
                    viewModel = authViewModel,
                    userViewModel = userViewModel,
                    deviceViewModel = deviceViewModel,
                    alertViewModel = alertViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}