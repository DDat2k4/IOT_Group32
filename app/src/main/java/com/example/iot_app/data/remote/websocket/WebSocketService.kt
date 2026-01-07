package com.example.iot_app.data.remote.websocket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.iot_app.R
import com.example.iot_app.data.remote.dto.AlertDto
import com.example.iot_app.data.remote.websocket.WebSocketManager
import com.example.iot_app.utils.NotificationHelper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class WebSocketService : Service() {

    private val webSocketManager = WebSocketManager()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var notificationHelper: NotificationHelper

    // Map lưu thời gian để chặn spam 90s
    private val notificationHistory = mutableMapOf<String, Long>()
    private val COOLDOWN_TIME = 90_000L

    companion object {
        const val CHANNEL_ID_SERVICE = "iot_background_service"
        const val ACTION_NEW_ALERT = "com.example.iot_app.ACTION_NEW_ALERT"
        const val EXTRA_ALERT_JSON = "extra_alert_json"
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val token = intent?.getStringExtra("TOKEN")
        val userId = intent?.getLongExtra("USER_ID", -1L) ?: -1L

        if (token != null && userId != -1L) {
            connectSocket(token, userId)
        }
        return START_STICKY
    }

    private fun connectSocket(token: String, userId: Long) {
        webSocketManager.onAlertReceived = { socketDto ->
            val uiAlert = socketDto.toAlertDto()
            handleNotificationLogic(uiAlert)
            broadcastToUI(uiAlert)
        }
        webSocketManager.connect(token, userId)
    }

    private fun handleNotificationLogic(newAlert: AlertDto) {
        if (newAlert.level == "HIGH" || newAlert.level == "MEDIUM") {
            val uniqueKey = "${newAlert.deviceCode}_${newAlert.alertType}"
            val currentTime = System.currentTimeMillis()
            val lastTime = notificationHistory[uniqueKey] ?: 0L

            if (currentTime - lastTime >= COOLDOWN_TIME) {
                showRichNotification(newAlert)
                notificationHistory[uniqueKey] = currentTime
                Log.d("IoTService", "Đã bắn thông báo cho $uniqueKey")
            }
        }
    }

    private fun broadcastToUI(alert: AlertDto) {
        val intent = Intent(ACTION_NEW_ALERT)
        intent.setPackage(packageName)
        intent.putExtra(EXTRA_ALERT_JSON, Gson().toJson(alert))
        sendBroadcast(intent)
    }

    // form thông báo
    private fun showRichNotification(alert: AlertDto) {
        // mức độ
        val levelVietnamese = when (alert.level) {
            "HIGH" -> "KHẨN CẤP"
            "MEDIUM" -> "NGUY HIỂM"
            else -> "Cảnh báo"
        }

        // Tiêu đề
        val location = alert.roomName ?: alert.deviceCode ?: "Khu vực"
        val title = "$levelVietnamese: $location"

        // Nhắc nhở
        val remind = when (alert.level) {
            "HIGH" -> "Khu vực không an toàn cần rời đi!"
            "MEDIUM" -> "Khu vực có vấn đề hãy kiểm tra!"
            else -> "Cảnh báo"
        }
        val unit = alert.sensorUnit ?: ""

        // Nội dung
        val content = """
            Khu vực: ${alert.deviceCode ?: "N/A"}
            Cảm biến: ${alert.alertType ?: "N/A"}
            Nhắc nhở: $remind
            -------------------------
            Giá trị đo: ${alert.value ?: 0} $unit
            Ngưỡng an toàn: ${alert.threshold ?: 0} $unit
            Thời gian: ${alert.createdAt.replace("T", " ").substringBefore(".")}
        """.trimIndent()

        notificationHelper.showNotification(title, content)
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_SERVICE, "IoT Background Service", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
            .setContentTitle("IoT Monitor")
            .setContentText("Đang giám sát hệ thống...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        webSocketManager.disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}