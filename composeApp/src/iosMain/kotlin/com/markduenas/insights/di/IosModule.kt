package com.markduenas.insights.di

import com.markduenas.insights.data.local.DatabaseDriverFactory
import com.markduenas.insights.platform.BillingService
import com.markduenas.insights.platform.PlatformSettings
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory().createDriver() }
    single { PlatformSettings() }
    single { BillingService() }
}

val allModules = commonModules + listOf(iosModule)
