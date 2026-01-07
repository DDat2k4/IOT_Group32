package com.example.iot_app.ui.dashboard

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_app.data.remote.dto.DeviceDto
import com.example.iot_app.data.remote.repository.AlertRepository
import com.example.iot_app.data.remote.repository.DeviceRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

// models
data class RoomUiModel(
    val deviceId: Long,
    val deviceCode: String,
    val roomName: String,
    val statusColor: Color,
    val sensors: List<SensorUiData>
)

data class SensorUiData(
    val id: Long,
    val type: String,
    val name: String,
    val value: Double,
    val threshold: Double,
    val unit: String
)

//viewmodel
class DashboardViewModel(
    private val deviceRepository: DeviceRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<RoomUiModel>>(emptyList())
    val rooms = _rooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //trạng thái biểu đồ
    private val _selectedChartType = MutableStateFlow("TEMP")
    val selectedChartType = _selectedChartType.asStateFlow()

    private val _chartEntries = MutableStateFlow<List<FloatEntry>>(emptyList())
    val chartEntries = _chartEntries.asStateFlow()

    private val _isChartLoading = MutableStateFlow(false)
    val isChartLoading = _isChartLoading.asStateFlow()

    private val _currentThreshold = MutableStateFlow(0.0)
    val currentThreshold = _currentThreshold.asStateFlow()

    private val _chartMaxY = MutableStateFlow(100f)
    val chartMaxY = _chartMaxY.asStateFlow()

    fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                val isFirstLoad = _rooms.value.isEmpty()
                loadDashboard(isSilent = !isFirstLoad)
                delay(5000)
            }
        }
    }

    suspend fun loadDashboard(isSilent: Boolean = false) = coroutineScope {
        if (!isSilent) {
            _isLoading.value = true
        }

        try {
            val devicesResult = deviceRepository.getDevices()

            if (devicesResult.isSuccess) {
                val devices = devicesResult.getOrDefault(emptyList())

                val roomList = devices.map { device ->
                    async { fetchRoomData(device) }
                }.awaitAll()

                _rooms.value = roomList
            }
        } catch (e: Exception) {
            Log.e("DashboardVM", "Lỗi load: ${e.message}")
        } finally {
            if (!isSilent) {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchRoomData(device: DeviceDto): RoomUiModel = coroutineScope {
        val deviceDetailResult = deviceRepository.getDeviceDetail(device.id.toLong())
        val deviceDetail = deviceDetailResult.getOrNull()

        val sensorConfigList = deviceDetail?.sensors ?: emptyList()
        val roomNameReal = deviceDetail?.roomName ?: device.name ?: "Phòng chưa đặt tên"

        val sensorUiList = sensorConfigList.map { config ->
            async {
                val logs = try {
                    alertRepository.getLatestSensorData(
                        topic = "iot/fire/${device.deviceCode}",
                        sensorType = config.sensorType ?: "",
                        limit = 1
                    )
                } catch (e: Exception) { emptyList() }

                val currentValue = logs.firstOrNull()?.value ?: 0.0

                SensorUiData(
                    id = config.id ?: 0L,
                    type = config.sensorType ?: "",
                    name = config.name ?: "Cảm biến",
                    value = currentValue,
                    threshold = config.maxValue ?: 0.0,
                    unit = config.unit ?: ""
                )
            }
        }.awaitAll()

        val color = calculateRoomColor(sensorUiList)

        RoomUiModel(
            deviceId = device.id.toLong(),
            deviceCode = device.deviceCode,
            roomName = roomNameReal,
            statusColor = color,
            sensors = sensorUiList
        )
    }

    private fun calculateRoomColor(sensors: List<SensorUiData>): Color {
        var maxSeverity = 0
        for (sensor in sensors) {
            if (sensor.threshold > 0) {
                val ratio = sensor.value / sensor.threshold
                if (ratio >= 1.0) {
                    return Color(0xFFD32F2F) // Đỏ
                } else if (ratio >= 0.8) {
                    maxSeverity = 1 // Cam
                }
            }
        }
        return when (maxSeverity) {
            1 -> Color(0xFFF9A825)
            else -> Color(0xFF388E3C)
        }
    }

    fun checkDashboardBadgeState(onResult: (Boolean) -> Unit) {
        val currentRooms = _rooms.value
        val hasDanger = currentRooms.any { it.statusColor != Color(0xFF388E3C) }
        onResult(hasDanger)
    }

    fun getRoomDetail(deviceCode: String): RoomUiModel? {
        return _rooms.value.find { it.deviceCode == deviceCode }
    }

    fun onChartSensorSelected(deviceCode: String, type: String) {
        _selectedChartType.value = type
        loadChartData(deviceCode, type)
    }

    // logic load biểu đ và tính ngưỡng
    private fun loadChartData(deviceCode: String, type: String) {
        viewModelScope.launch {
            _isChartLoading.value = true
            _chartEntries.value = emptyList()

            try {
                // 1. Gọi API lấy dữ liệu
//                val logs = alertRepository.getLatestSensorData(
//                    topic = "iot/fire/$deviceCode",
//                    sensorType = type,
//                    limit = 20
//                )
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

                // Lấy thời gian hiện tại
                val calendar = Calendar.getInstance()
                val toStr = sdf.format(calendar.time)

                // Lùi lại 1 phút
                calendar.add(Calendar.MINUTE, -1)
                val fromStr = sdf.format(calendar.time)

                // api vẽ trong tgian từ 1 phút trước đến hiện tại
                val logs = alertRepository.getChartDataByTime(
                    topic = "iot/fire/$deviceCode",
                    sensorType = type,
                    from = fromStr,
                    to = toStr
                )

                // tìm ngưỡng của biểu đồ
                val currentRoom = _rooms.value.find { it.deviceCode == deviceCode }
                val currentSensor = currentRoom?.sensors?.find { it.type == type }
                val thresholdVal = currentSensor?.threshold ?: 0.0
                _currentThreshold.value = thresholdVal

                // tính max Y
                val maxDataValue = logs.maxOfOrNull { it.value } ?: 50.0
                // Max Y lớn hơn data và ngưỡng, x1.2 giá trị lowns nhất có
                val calculatedMaxY = max(maxDataValue, thresholdVal).toFloat() * 1.2f
                _chartMaxY.value = if (calculatedMaxY > 0) calculatedMaxY else 100f

                // map dữ liệu
                val entries = logs.reversed().mapIndexed { index, log ->
                    FloatEntry(x = index.toFloat(), y = log.value.toFloat())
                }
                _chartEntries.value = entries

            } catch (e: Exception) {
                Log.e("Chart", "Lỗi tải chart: ${e.message}")
            } finally {
                _isChartLoading.value = false
            }
        }
    }

    fun initChart(deviceCode: String) {
        // Luôn reset về TEMP và tải lại khi vào màn hình mới
        _selectedChartType.value = "TEMP"
        loadChartData(deviceCode, "TEMP")
    }
}