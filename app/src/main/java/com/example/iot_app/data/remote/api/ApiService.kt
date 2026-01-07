package com.example.iot_app.data.remote.api

import com.example.iot_app.data.remote.dto.AlertDto
import com.example.iot_app.data.remote.dto.ApiResponse
import com.example.iot_app.data.remote.dto.AuthData
import com.example.iot_app.data.remote.dto.ChangePasswordRequest
import com.example.iot_app.data.remote.dto.DeviceDto
import com.example.iot_app.data.remote.dto.DeviceRequest
import com.example.iot_app.data.remote.dto.ForgotPasswordRequest
import com.example.iot_app.data.remote.dto.LoginRequest
import com.example.iot_app.data.remote.dto.LogoutRequest
import com.example.iot_app.data.remote.dto.MqttRequest
import com.example.iot_app.data.remote.dto.RefreshTokenRequest
import com.example.iot_app.data.remote.dto.RegisterRequest
import com.example.iot_app.data.remote.dto.ResetPasswordRequest
import com.example.iot_app.data.remote.dto.SensorConfigDto
import com.example.iot_app.data.remote.dto.SensorDto
import com.example.iot_app.data.remote.dto.SensorLogDto
import com.example.iot_app.data.remote.dto.SensorRequest
import com.example.iot_app.data.remote.dto.UpdateProfileRequest
import com.example.iot_app.data.remote.dto.UserProfileDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // xác thực với các API này
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<Unit>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthData>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @POST("api/auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<Unit>

    @POST("api/auth/refresh-token")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<ApiResponse<AuthData>>

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest)

    // user profile
    @GET("api/user/me")
    suspend fun getMyProfile(): UserProfileDto

    @PUT("api/user/me")
    suspend fun updateMyProfile(@Body body: UpdateProfileRequest): UserProfileDto

    //device
    @GET("api/devices")
    suspend fun getAllDevices(): List<DeviceDto>

    @POST("api/devices")
    suspend fun createDevice(@Body req: DeviceRequest): DeviceDto

    @PUT("api/devices/{id}")
    suspend fun updateDevice(@Path("id") id: Int, @Body req: DeviceRequest): DeviceDto

    @DELETE("api/devices/{id}")
    suspend fun deleteDevice(@Path("id") id: Int): retrofit2.Response<Unit>

    // cảm biến
    @GET("api/sensors/device/{id}")
    suspend fun getDeviceSensors(@Path("id") id: Long): List<SensorConfigDto>

    @POST("api/sensors/device/{deviceId}")
    suspend fun createSensor(@Path("deviceId") deviceId: Int, @Body req: SensorRequest): SensorDto

    @PUT("api/sensors/{id}")
    suspend fun updateSensor(@Path("id") id: Int, @Body req: SensorRequest): SensorDto

    //cảnh báo
    @GET("api/alerts")
    suspend fun getAlerts(): List<AlertDto>

    @GET("api/alerts/device/{deviceId}")
    suspend fun getAlertsByDevice(@Path("deviceId") id: Long): List<AlertDto>

    @GET("api/alerts/user/{userId}")
    suspend fun getAlertsByUser(@Path("userId") userId: Long): List<AlertDto>

    //API để value của cảm biến gần nhất
    @GET("api/message-log/chart")
    suspend fun getLatestSensorData(
        @Query("topic") topic: String,
        @Query("sensorType") type: String,
        @Query("limit") limit: Int = 1
    ): List<SensorLogDto>

    // API lấy dữ liệu theo khoảng tgian, cho vẽ biểu đồ
    @GET("api/message-log/chart")
    suspend fun getChartDataByTime(
        @Query("topic") topic: String,
        @Query("sensorType") type: String,
        @Query("from") from: String, // Format: yyyy-MM-dd'T'HH:mm:ss
        @Query("to") to: String
    ): List<SensorLogDto>

    //gửi mqtt cho phần cập nhật trạng thái hoặc ngưỡng của cảnh báo
    @POST("api/mqtt/send")
    suspend fun sendMqtt(@Body request: MqttRequest): Response<Unit>
}