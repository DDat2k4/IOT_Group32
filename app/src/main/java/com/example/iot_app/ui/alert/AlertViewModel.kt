package com.example.iot_app.ui.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_app.data.remote.dto.AlertDto
import com.example.iot_app.data.remote.repository.AlertRepository
import com.example.iot_app.data.remote.websocket.WebSocketService
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(
    private val repository: AlertRepository,
    private val context: Context
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<AlertDto>>(emptyList())
    val alerts = _alerts.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // có cảnh báo mới
    private val _showNewAlertIndicator = MutableStateFlow(false)
    val showNewAlertIndicator = _showNewAlertIndicator.asStateFlow()

    // lắng nghe từ service
    private val alertReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WebSocketService.ACTION_NEW_ALERT) {
                // nhận json từ service
                val json = intent.getStringExtra(WebSocketService.EXTRA_ALERT_JSON)
                if (json != null) {
                    try {
                        val newAlert = Gson().fromJson(json, AlertDto::class.java)

                        // cập nhật danh sách
                        val currentList = _alerts.value.toMutableList()
                        currentList.add(0, newAlert)
                        _alerts.value = currentList

                        // khôi phục lại trạng thái có cảnh báo mới
                        _showNewAlertIndicator.value = true

                        Log.d("AlertViewModel", "UI đã cập nhật & Bật thông báo mới: ${newAlert.deviceCode}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    init {
        // đky lắng nghe
        val filter = IntentFilter(WebSocketService.ACTION_NEW_ALERT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(alertReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(alertReceiver, filter)
        }
    }

    fun connectSocketWithUser(userId: Long) {
        loadAlertsFromApi(userId)
    }

    fun refreshData() {
        _showNewAlertIndicator.value = false
    }

    private fun loadAlertsFromApi(userId: Long) {
        viewModelScope.launch {
            _loading.value = true
            repository.getAlerts(userId).onSuccess { list ->
                _alerts.value = list.sortedByDescending { it.createdAt }
                _showNewAlertIndicator.value = false
            }
            _loading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(alertReceiver)
        } catch (e: Exception) { e.printStackTrace() }
    }
}