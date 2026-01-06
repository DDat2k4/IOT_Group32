package com.example.iot_app.data.remote.dto

data class UserProfileDto(
    val id: Long,
    val username: String,
    val fullName: String,
    val email: String,
    val role: String
)
