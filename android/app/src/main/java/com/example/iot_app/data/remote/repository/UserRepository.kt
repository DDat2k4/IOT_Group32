package com.example.iot_app.data.remote.repository

import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.ChangePasswordRequest
import com.example.iot_app.data.remote.dto.UpdateProfileRequest
import com.example.iot_app.data.remote.dto.UserProfileDto

class UserRepository(
    private val api: ApiService
) {

    suspend fun getProfile(): Result<UserProfileDto> =
        runCatching { api.getMyProfile() }

    suspend fun updateProfile(
        fullName: String,
        email: String
    ): Result<UserProfileDto> =
        runCatching {
            api.updateMyProfile(UpdateProfileRequest(fullName, email))
        }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit> =
        runCatching {
            api.changePassword(
                ChangePasswordRequest(oldPassword, newPassword)
            )
        }
}
