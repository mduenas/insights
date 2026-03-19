package com.markduenas.insights.di

import com.markduenas.insights.data.local.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()).createDriver() }
}

val allModules = commonModules + listOf(androidModule)
