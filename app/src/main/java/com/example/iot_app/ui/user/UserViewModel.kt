package com.example.iot_app.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.dto.UserProfileDto
import com.example.iot_app.data.remote.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Chuyển LiveData -> StateFlow để dùng collectAsState() bên Compose
    private val _profile = MutableStateFlow<UserProfileDto?>(null)
    val profile = _profile.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        // Tự động tải profile ngay khi ViewModel được khởi tạo
        // Để MainScreen có id ngay lập tức mà kết nối Socket
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true
            repo.getProfile()
                .onSuccess { _profile.value = it }
                .onFailure { _message.value = it.message }
            _loading.value = false
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            _loading.value = true
            repo.updateProfile(fullName, email)
                .onSuccess {
                    _profile.value = it
                    _message.value = "Cập nhật thành công"
                }
                .onFailure { _message.value = it.message }
            _loading.value = false
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _loading.value = true
            repo.changePassword(oldPass, newPass)
                .onSuccess { _message.value = "Đổi mật khẩu thành công" }
                .onFailure { _message.value = it.message }
            _loading.value = false
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun clearData() {
        // 1. Xóa dữ liệu trong ViewModel
        _profile.value = null
        _message.value = null
     //   _loading.value = false
        // 2. Xóa Token trong máy (để API sau không dùng lại token cũ)
        tokenManager.clear()
    }
}