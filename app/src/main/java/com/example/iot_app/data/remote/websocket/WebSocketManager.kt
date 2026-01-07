package com.example.iot_app.data.remote.websocket

import android.annotation.SuppressLint
import android.util.Log
import com.example.iot_app.data.remote.dto.AlertSocketDto
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader

class WebSocketManager {

    private var stompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private val gson = Gson()

    var onAlertReceived: ((AlertSocketDto) -> Unit)? = null

    // IP máy ảo
    private val BASE_SOCKET_URL = "ws://10.0.2.2:8080/ws-alert/websocket"

    @SuppressLint("CheckResult")
    fun connect(accessToken: String, userId: Long) {
        if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        disconnect()

        val urlWithToken = "$BASE_SOCKET_URL?access_token=$accessToken"
        Log.e("DEBUG_SOCKET", "1. Connecting: $urlWithToken")

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, urlWithToken)
            .withClientHeartbeat(10000)
            .withServerHeartbeat(10000)

        val headers = listOf(StompHeader("Authorization", "Bearer $accessToken"))

        val lifecycleDisposable = stompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ lifecycleEvent ->
                when (lifecycleEvent.type) {
                    ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED -> {
                        Log.e("DEBUG_SOCKET", "OPENED! Đang subscribe...")
                        subscribeToUserQueue()
                    }
                    ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR -> {
                        Log.e("DEBUG_SOCKET", "ERROR CONNECT: ${lifecycleEvent.exception}")
                    }
                    ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED -> {
                        Log.e("DEBUG_SOCKET", "CLOSED")
                    }
                    else -> {}
                }
            }, { err -> Log.e("DEBUG_SOCKET", "ERROR LIFECYCLE", err) })

        compositeDisposable?.add(lifecycleDisposable)
        stompClient?.connect(headers)
    }

    private fun subscribeToUserQueue() {
        // Đường dẫn chuẩn
        val path = "/user/queue/alerts"
        Log.e("DEBUG_SOCKET", "3. Subscribing to: $path")

        val topicDisposable = stompClient!!.topic(path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage ->
                val payload = topicMessage.payload
                Log.e("DEBUG_SOCKET", "NHẬN ĐƯỢC DATA THÔ: $payload")

                try {
                    val alert = gson.fromJson(payload, AlertSocketDto::class.java)
                    Log.e("DEBUG_SOCKET", "Parse OK: Device=${alert.deviceName}")
                    onAlertReceived?.invoke(alert)
                } catch (e: Exception) {
                    Log.e("DEBUG_SOCKET", "LỖI PARSE JSON: ${e.message}")
                }
            }, { throwable ->
                Log.e("DEBUG_SOCKET", "LỖI SUBSCRIBE", throwable)
            })

        compositeDisposable?.add(topicDisposable)
    }

    fun disconnect() {
        stompClient?.disconnect()
        compositeDisposable?.dispose()
        compositeDisposable = null
    }
}