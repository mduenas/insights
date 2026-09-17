package com.markduenas.insights.platform

import android.content.Context

actual class PlatformSettings(context: Context) {
    private val prefs = context.getSharedPreferences("kindling_settings", Context.MODE_PRIVATE)

    actual fun getString(key: String, default: String): String =
        prefs.getString(key, default) ?: default

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}
