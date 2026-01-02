package com.example.iot_app.data.remote.dto

data class AlertDto(
    val id: Long,
    val deviceId: Long,
    val sensorId: Long,
    val message: String,
    val level: String, // "LOW", "MEDIUM", "HIGH"
    val createdAt: String
)