package com.example.iot_app.data.remote.dto

// POST /api/sensors/...
data class SensorRequest(
    val sensorType: String,
    val name: String,
    val unit: String,
    val minValue: Double,
    val maxValue: Double,
    val status: String = "ACTIVE"
)