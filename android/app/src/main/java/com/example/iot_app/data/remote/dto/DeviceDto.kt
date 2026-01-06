package com.example.iot_app.data.remote.dto

// GET /api/devices
data class DeviceDto(
    val id: Int,
    val deviceCode: String,
    val name: String,     // Phòng
    val location: String, // Nhà/Vị trí
    val status: String    // ACTIVE/INACTIVE
)