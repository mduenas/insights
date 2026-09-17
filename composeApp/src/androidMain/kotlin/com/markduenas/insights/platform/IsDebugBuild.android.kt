package com.markduenas.insights.platform

import com.markduenas.insights.BuildConfig

actual fun isDebugBuild(): Boolean = BuildConfig.DEBUG
