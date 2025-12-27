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
import com.example.iot_app.data.remote.repository.UserRepository
import com.example.iot_app.navigation.AppNavGraph
import com.example.iot_app.ui.auth.AuthViewModel
import com.example.iot_app.ui.user.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Trong onCreate
        val api = RetrofitClient.getInstance(applicationContext)
        val tokenManager = TokenManager(applicationContext)
        val repo = AuthRepository(api, tokenManager)
        val userRepo = UserRepository(api)
        setContent {

            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AuthViewModel(repo)
                    }
                }
            )

            val userViewModel: UserViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { UserViewModel(userRepo) }
                }
            )

            MaterialTheme {
                AppNavGraph(authViewModel, userViewModel)
            }
        }
    }
}
