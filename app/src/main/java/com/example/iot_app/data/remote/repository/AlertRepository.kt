package com.example.iot_app.data.remote.repository

import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.AlertDto
import com.example.iot_app.data.remote.dto.SensorLogDto

class AlertRepository(private val api: ApiService) {
    // Hàm để lấy danh sách các thông báo
    suspend fun getAlerts(userId: Long): Result<List<AlertDto>> {
        return runCatching {
            // theo đúng user id
            api.getAlertsByUser(userId).sortedByDescending { it.createdAt }
        }
    }
    // lấy số lượng value gần nhất
    suspend fun getLatestSensorData(topic: String, sensorType: String, limit: Int): List<SensorLogDto> {
        return api.getLatestSensorData(topic, sensorType, limit)
    }
    //theo time vẽ biểu đồ
    suspend fun getChartDataByTime(topic: String, sensorType: String, from: String, to: String): List<SensorLogDto> {
        return api.getChartDataByTime(topic, sensorType, from, to)
    }
}