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
    val sensors = _sensors.asStateFlow()

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
                    loadDevices()
                }
                .onFailure { _message.value = "Lỗi thêm: ${it.message}" }
            _loading.value = false
        }
    }

    // Thêm logic gửi MQTT Status
    fun updateDevice(id: Int, code: String, name: String, location: String, status: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Cập nhật vào Database trước
                val deviceReq = DeviceRequest(code, name, location, status)
                val deviceUpdateResult = repository.updateDevice(id, deviceReq)

                if (deviceUpdateResult.isSuccess) {
                    // Đồng bộ trạng thái Sensors trong DB
                    val sensorsResult = repository.getSensors(id)
                    if (sensorsResult.isSuccess) {
                        val currentSensors = sensorsResult.getOrThrow()
                        currentSensors.forEach { sensor ->
                            val sensorReq = SensorRequest(
                                sensorType = sensor.sensorType,
                                name = sensor.name,
                                unit = sensor.unit,
                                minValue = sensor.minValue,
                                maxValue = sensor.maxValue,
                                status = status // Sensor theo status của Device
                            )
                            repository.updateSensor(sensor.id, sensorReq)
                        }
                    }

                    // gửi lệnh MQTT cập nhật trạng thái
                    val topic = "iot/status/$code"
                    val payload = mapOf("value" to status)

                    // Gọi hàm API
                    repository.sendMqttCommand(topic, payload)

                    _message.value = "Cập nhật thiết bị & Gửi lệnh MQTT thành công!"
                    loadDevices()
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

    fun loadSensorsForDevice(deviceId: Int) {
        viewModelScope.launch {
            _sensors.value = emptyList()
            repository.getSensors(deviceId)
                .onSuccess { _sensors.value = it }
                .onFailure { error ->
                    _message.value = "Lỗi tải sensor: ${error.message}"
                    error.printStackTrace()
                }
        }
    }

    //thêm tham số deviceCode để gửi MQTT Threshold
    fun updateThreshold(deviceCode: String, sensor: SensorDto, newVal: Double) {
        viewModelScope.launch {
            // cập nhật vào Database
            repository.updateSensorThreshold(sensor, newVal)
                .onSuccess { updatedSensor ->

                    // gửi mqtt
                    val topic = "iot/threshold/$deviceCode"
                    val payload = mapOf(
                        "sensorType" to sensor.sensorType,
                        "threshold" to newVal
                    )
                    repository.sendMqttCommand(topic, payload)

                    _message.value = "Đã cập nhật ngưỡng & Gửi lệnh MQTT"

                    //cập nhật UI cục bộ
                    val currentList = _sensors.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == sensor.id }
                    if (index != -1) {
                        currentList[index] = updatedSensor
                        _sensors.value = currentList
                    }
                }
                .onFailure { _message.value = "Lỗi cập nhật: ${it.message}" }
        }
    }

    fun clearMessage() { _message.value = null }
}