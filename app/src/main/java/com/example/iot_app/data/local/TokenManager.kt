package com.example.iot_app.data.local

import android.content.Context

class TokenManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("iot_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("access_token", null)
    }
}
