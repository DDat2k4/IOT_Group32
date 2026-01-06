package com.example.iot_app.data.local

import android.content.Context

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("iot_prefs", Context.MODE_PRIVATE)

    fun saveTokens(access: String?, refresh: String?) {
        prefs.edit()
            .putString("access_token", access)
            .putString("refresh_token", refresh)
            .apply()
    }

    fun getToken(): String? = prefs.getString("access_token", null)
    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun clear() = prefs.edit().clear().apply()
}
