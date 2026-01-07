package com.example.iot_app.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.iot_app.ui.dashboard.RoomUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    // chấm đỏ ở dashboard
    private val _showDashboardBadge = MutableStateFlow(false)
    val showDashboardBadge = _showDashboardBadge.asStateFlow()

    // màu xanh thì an toàn
    fun updateDashboardBadge(rooms: List<RoomUiModel>) {
        val safeColor = Color(0xFF388E3C) // Xanh lá đậm (An toàn)

        // k xanh thì cảnh báo chấm đỏ
        val hasDanger = rooms.any { it.statusColor != safeColor }
        _showDashboardBadge.value = hasDanger
    }
}