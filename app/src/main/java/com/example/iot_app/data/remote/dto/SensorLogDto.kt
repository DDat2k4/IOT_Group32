package com.example.iot_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SensorLogDto(
    @SerializedName("id") val id: Long,
    @SerializedName("value") val value: Double,
    @SerializedName("threshold") val threshold: Double,
    @SerializedName("createdAt") val createdAt: String
)