package com.example.iot_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.api.RetrofitClient
import com.example.iot_app.data.remote.repository.AuthRepository
import com.example.iot_app.navigation.AppNavGraph
import com.example.iot_app.ui.auth.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ TokenManager
        val tokenManager = TokenManager(applicationContext)

        // 2️⃣ ApiService (đã gắn AuthInterceptor)
        val api = RetrofitClient.create(applicationContext)

        // 3️⃣ Repository (ĐÚNG constructor)
        val repo = AuthRepository(api, tokenManager)

        setContent {

            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AuthViewModel(repo)
                    }
                }
            )

            MaterialTheme {
                AppNavGraph(authViewModel)
            }
        }
    }
}
