package com.example.iot_app.data.remote.dto

// POST /api/devices
data class DeviceRequest(
    val deviceCode: String,
    val name: String,
    val location: String,
    val status: String = "ACTIVE"
)
