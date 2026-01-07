package com.example.iot_app.data.remote.dto

data class MqttRequest(
    val topic: String,
    val payload: Any // tùy ý payload để gửi nhiều loại
)