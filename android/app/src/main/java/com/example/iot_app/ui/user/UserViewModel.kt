package com.example.iot_app.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_app.data.remote.dto.UserProfileDto
import com.example.iot_app.data.remote.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _profile = MutableLiveData<UserProfileDto>()
    val profile: LiveData<UserProfileDto> = _profile

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun loadProfile() {
        viewModelScope.launch {
            repo.getProfile()
                .onSuccess { _profile.value = it }
                .onFailure { _message.value = it.message }
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
        _profile.value = null
        _message.value = null
        _loading.value = false
    }
}
