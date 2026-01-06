package com.example.iot_app.data.remote.dto

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
