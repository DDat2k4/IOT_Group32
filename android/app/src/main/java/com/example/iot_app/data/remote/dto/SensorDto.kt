package com.example.iot_app.data.remote.dto

// GET /api/sensors/...
data class SensorDto(
    val id: Int,
    val sensorType: String, // TEMP, MQ2, FLAME, CO
    val name: String,
    val unit: String,
    val minValue: Double,
    val maxValue: Double, // Ngưỡng
    val status: String
)