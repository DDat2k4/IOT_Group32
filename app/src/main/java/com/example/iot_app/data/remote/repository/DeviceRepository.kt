package com.example.iot_app.data.remote.repository

import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.*

class DeviceRepository(private val api: ApiService) {

    suspend fun getDevices(): Result<List<DeviceDto>> = runCatching { api.getAllDevices() }

    // logic: tạo Device thì tạo 4 Sensor đi cùng nó
    suspend fun createDeviceWithSensors(
        deviceCode: String,
        name: String,
        location: String
    ): Result<Unit> {
        return runCatching {
            // Tạo Device trước
            val deviceReq = DeviceRequest(deviceCode, name, location)
            val createdDevice = api.createDevice(deviceReq)
            val deviceId = createdDevice.id

            //  4 sensor mặc định
            val defaultSensors = listOf(
                SensorRequest("TEMP", "Nhiệt độ", "°C", 0.0, 50.0), // Ngưỡng mặc định 50
                SensorRequest("MQ2", "Khí Gas", "ppm", 0.0, 300.0), // Ngưỡng 300
                SensorRequest("FLAME", "Lửa", "bool", 0.0, 1.0),
                SensorRequest("CO", "Khí CO", "ppm", 0.0, 100.0)
            )
            // gọi api tạo sensor
            defaultSensors.forEach { sensorReq ->
                api.createSensor(deviceId, sensorReq)
            }
        }
    }

    suspend fun updateDevice(id: Int, req: DeviceRequest): Result<DeviceDto> {
        return runCatching {
            api.updateDevice(id, req)
        }
    }

    suspend fun deleteDevice(id: Int): Result<Unit> = runCatching {
        api.deleteDevice(id)
    }

    suspend fun getSensors(deviceId: Int): Result<List<SensorDto>> = runCatching {
        api.getSensorsByDeviceId(deviceId)
    }

    suspend fun updateSensor(id: Int, req: SensorRequest): Result<SensorDto> = runCatching {
        api.updateSensor(id, req)
    }

    // Hàm cập nhật ngưỡng sensor
    suspend fun updateSensorThreshold(sensor: SensorDto, newThreshold: Double): Result<SensorDto> {
        return runCatching {
            // chỉnh sửa ngưỡng max
            val req = SensorRequest(
                sensorType = sensor.sensorType,
                name = sensor.name,
                unit = sensor.unit,
                minValue = sensor.minValue,
                maxValue = newThreshold, // Cập nhật ngưỡng mới
                status = sensor.status
            )
            api.updateSensor(sensor.id, req)
        }
    }
}