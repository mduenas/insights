package com.markduenas.insights.di

import com.markduenas.insights.data.local.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory().createDriver() }
}

val allModules = commonModules + listOf(iosModule)
