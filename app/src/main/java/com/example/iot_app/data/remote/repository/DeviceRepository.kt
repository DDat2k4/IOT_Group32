package com.example.iot_app.data.remote.repository

import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.*

class DeviceRepository(private val api: ApiService) {

    suspend fun getDevices(): Result<List<DeviceDto>> = runCatching { api.getAllDevices() }

    suspend fun createDeviceWithSensors(deviceCode: String, name: String, location: String): Result<Unit> {
        return runCatching {
            val deviceReq = DeviceRequest(deviceCode, name, location)
            val createdDevice = api.createDevice(deviceReq)
            val deviceId = createdDevice.id

            val defaultSensors = listOf(
                SensorRequest("TEMP", "Nhiệt độ", "°C", 0.0, 60.0),
                SensorRequest("MQ2", "Khí Gas", "ppm", 0.0, 800.0),
                SensorRequest("FLAME", "Lửa", "", 0.0, 1.0),
                SensorRequest("CO", "Khí CO", "ppm", 0.0, 700.0)
            )
            defaultSensors.forEach { sensorReq -> api.createSensor(deviceId, sensorReq) }
        }
    }

    suspend fun updateDevice(id: Int, req: DeviceRequest): Result<DeviceDto> = runCatching {
        api.updateDevice(id, req)
    }

    suspend fun deleteDevice(id: Int): Result<Unit> = runCatching {
        api.deleteDevice(id)
    }

    suspend fun getSensors(deviceId: Int): Result<List<SensorDto>> {
        return runCatching {
            // Gọi API trả về danh sách
            val sensorConfigs = api.getDeviceSensors(deviceId.toLong())

            // map sang SensorDto
            sensorConfigs.map { config ->
                SensorDto(
                    id = (config.id ?: 0L).toInt(),
                    sensorType = config.sensorType ?: "",
                    name = config.name ?: "Cảm biến",
                    unit = config.unit ?: "",
                    minValue = 0.0,
                    maxValue = config.maxValue ?: 0.0,
                    status = config.status ?: "ACTIVE"
                )
            }
        }
    }

    // dashboard
    suspend fun getDeviceDetail(deviceId: Long): Result<DeviceDetailDto> {
        return runCatching {
            // trả về danh sách các cảm biến của thiết bị
            val sensorConfigs = api.getDeviceSensors(deviceId)
            // lấy tên phòng và mã thiết bị để ghi lên dashboard
            DeviceDetailDto(
                id = deviceId,
                deviceCode = "",
                roomName = null,
                sensors = sensorConfigs
            )
        }
    }

    suspend fun updateSensor(id: Int, req: SensorRequest): Result<SensorDto> = runCatching {
        api.updateSensor(id, req)
    }

    suspend fun updateSensorThreshold(sensor: SensorDto, newThreshold: Double): Result<SensorDto> {
        return runCatching {
            val req = SensorRequest(
                sensorType = sensor.sensorType,
                name = sensor.name,
                unit = sensor.unit,
                minValue = sensor.minValue,
                maxValue = newThreshold,
                status = sensor.status
            )
            api.updateSensor(sensor.id, req)
        }
    }

    suspend fun sendMqttCommand(topic: String, payload: Map<String, Any>) {
        try {
            api.sendMqtt(MqttRequest(topic, payload))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}