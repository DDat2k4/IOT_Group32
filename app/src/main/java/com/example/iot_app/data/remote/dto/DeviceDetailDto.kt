package com.example.iot_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeviceDetailDto(
    val id: Long?,
    val deviceCode: String?,
    @SerializedName("name") val roomName: String?,
    val sensors: List<SensorConfigDto>?
)