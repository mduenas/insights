package com.markduenas.insights.di

import com.markduenas.insights.data.local.DatabaseDriverFactory
import com.markduenas.insights.platform.BillingService
import com.markduenas.insights.platform.PlatformSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()).createDriver() }
    single { PlatformSettings(androidContext()) }
    single { BillingService(androidContext()) }
}

val allModules = commonModules + listOf(androidModule)
