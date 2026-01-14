package com.example.iot_app.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_app.data.remote.dto.DeviceDto
import com.example.iot_app.data.remote.dto.DeviceRequest
import com.example.iot_app.data.remote.dto.SensorDto
import com.example.iot_app.data.remote.dto.SensorRequest
import com.example.iot_app.data.remote.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceViewModel(private val repository: DeviceRepository) : ViewModel() {

    private val _devices = MutableStateFlow<List<DeviceDto>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _sensors = MutableStateFlow<List<SensorDto>>(emptyList())
    val sensors = _sensors.asStateFlow() // ds sensor của thiết bị đang chọn

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun loadDevices() {
        viewModelScope.launch {
            _loading.value = true
            repository.getDevices()
                .onSuccess { _devices.value = it }
                .onFailure { _message.value = "Lỗi tải thiết bị: ${it.message}" }
            _loading.value = false
        }
    }

    fun addDevice(code: String, name: String, location: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.createDeviceWithSensors(code, name, location)
                .onSuccess {
                    _message.value = "Thêm thiết bị & Sensors thành công!"
                    loadDevices() // Load lại danh sách
                }
                .onFailure { _message.value = "Lỗi thêm: ${it.message}" }
            _loading.value = false
        }
    }

    fun updateDevice(id: Int, code: String, name: String, location: String, status: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // cập nhật theiest bị
                val deviceReq = DeviceRequest(code, name, location, status)
                val deviceUpdateResult = repository.updateDevice(id, deviceReq)

                if (deviceUpdateResult.isSuccess) {
                    // lấy ds sensor đi cùng thiết bị đó
                    val sensorsResult = repository.getSensors(id)

                    if (sensorsResult.isSuccess) {
                        val currentSensors = sensorsResult.getOrThrow()

                        // duyệt qua sensor, lấy status theo device của các sensor đó
                        currentSensors.forEach { sensor ->
                            val sensorReq = SensorRequest(
                                sensorType = sensor.sensorType,
                                name = sensor.name,
                                unit = sensor.unit,
                                minValue = sensor.minValue,
                                maxValue = sensor.maxValue,
                                status = status // dựa theo divice
                            )
                            repository.updateSensor(sensor.id, sensorReq)
                        }
                    }
                    _message.value = "Cập nhật thiết bị và đồng bộ cảm biến thành công!"
                    loadDevices() // load lại UI
                } else {
                    _message.value = "Lỗi cập nhật thiết bị: ${deviceUpdateResult.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi hệ thống: ${e.message}"
            }
            _loading.value = false
        }
    }

    fun deleteDevice(id: Int) {
        viewModelScope.launch {
            repository.deleteDevice(id)
                .onSuccess {
                    _message.value = "Đã xóa thiết bị"
                    loadDevices()
                }
                .onFailure { _message.value = "Lỗi xóa: ${it.message}" }
        }
    }

    // xem thiết bị thì hàm này để lấy list sensor hiển thị lên
    fun loadSensorsForDevice(deviceId: Int) {
        viewModelScope.launch {
            _sensors.value = emptyList() // Reset trước
            repository.getSensors(deviceId)
                .onSuccess { _sensors.value = it }
                .onFailure { _message.value = "Không tải được sensor" }
        }
    }

    fun updateThreshold(sensor: SensorDto, newVal: Double) {
        viewModelScope.launch {
            repository.updateSensorThreshold(sensor, newVal)
                .onSuccess {
                    _message.value = "Đã cập nhật ngưỡng"
                    // Load lại list sensor để UI cập nhật, update cục bộ
                    val currentList = _sensors.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == sensor.id }
                    if (index != -1) {
                        currentList[index] = it
                        _sensors.value = currentList
                    }
                }
                .onFailure { _message.value = "Lỗi cập nhật: ${it.message}" }
        }
    }

    fun clearMessage() { _message.value = null }
}