package com.example.iot_app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_app.data.remote.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Error(val message: String) : LoginState()
    object Success : LoginState()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.login(username, password)
            _loading.value = false

            result.onSuccess {
                _message.value = "Đăng nhập thành công"
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            val result = repository.forgotPassword(email)
            _message.value = result.fold(
                onSuccess = { "Đã gửi OTP về email" },
                onFailure = { it.message }
            )
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            val result = repository.resetPassword(email, otp, newPassword)
            _message.value = result.fold(
                onSuccess = { "Đổi mật khẩu thành công" },
                onFailure = { it.message }
            )
        }
    }
}
