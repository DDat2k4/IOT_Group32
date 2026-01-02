package com.example.iot_app.data.repository

import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.AlertDto

class AlertRepository(private val api: ApiService) {

    suspend fun getAlerts(): Result<List<AlertDto>> {
        return runCatching {
            // Sắp xếp giảm dần theo thời gian (giả sử backend chưa sort)
            // Nếu backend sort rồi thì bỏ đoạn sortedByDescending
            api.getAlerts().sortedByDescending { it.createdAt }
        }
    }
}