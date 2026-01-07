package com.example.iot_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AlertSocketDto(
    @SerializedName("alertType") val alertType: String?,
    @SerializedName("alertLevel") val alertLevel: String,
    @SerializedName("value") val value: Double,
    @SerializedName("threshold") val threshold: Double,
    @SerializedName("deviceName") val deviceName: String?,
    @SerializedName("name") val roomName: String? = null,      // Tên phòng
    @SerializedName("sensorUnit") val sensorUnit: String? = null, // Đơn vị
    @SerializedName("sensorName") val sensorName: String? = null,

    @SerializedName("createdAt") val createdAt: String
) {
    fun toAlertDto(): AlertDto {
        return AlertDto(
            id = System.currentTimeMillis(),

            //map dữ liệu
            deviceCode = deviceName,
            sensorType = sensorName ?: "Cảm biến",
            sensorUnit = sensorUnit ?: "",
            value = value,
            threshold = threshold,
            alertType = alertType ?: "Cảnh báo",
            level = alertLevel,
            createdAt = createdAt
        )
    }
}