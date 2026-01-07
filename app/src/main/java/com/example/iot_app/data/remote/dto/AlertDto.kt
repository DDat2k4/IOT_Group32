package com.example.iot_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AlertDto(
    val id: Long,

    @SerializedName("deviceCode")
    val deviceCode: String? = null, // mã tb do nhóm cấp

    @SerializedName("name")
    val roomName: String? = null,   // phòng

    @SerializedName("sensorType")
    val sensorType: String? = null, // CO/TEMP/MQ2/FLAME

    @SerializedName("sensorUnit")
    val sensorUnit: String? = null,

    val value: Double? = null,      // số đo của sensor
    val threshold: Double? = null,

    @SerializedName("alertType")
    val alertType: String? = null,  // tên của cảm biến Cảm biến lửa

    val level: String,              // MEDIUM hoặc HIGH
    val createdAt: String
)