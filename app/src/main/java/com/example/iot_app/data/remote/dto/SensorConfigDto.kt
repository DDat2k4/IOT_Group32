package com.example.iot_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SensorConfigDto(
    val id: Long?,
    val sensorType: String?,
    val name: String?,
    val unit: String?,
    @SerializedName("maxValue") val maxValue: Double?,
    val status: String?
)