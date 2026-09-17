package com.markduenas.insights.platform

import platform.Foundation.NSUserDefaults

actual class PlatformSettings {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getString(key: String, default: String): String =
        defaults.stringForKey(key) ?: default

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        if (defaults.objectForKey(key) == null) return default
        return defaults.boolForKey(key)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }
}
