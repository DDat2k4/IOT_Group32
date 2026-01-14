package com.example.iot_app.data.remote.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val email: String
)