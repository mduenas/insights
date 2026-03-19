package com.markduenas.insights

import androidx.compose.ui.window.ComposeUIViewController
import com.markduenas.insights.di.allModules
import org.koin.core.context.startKoin

fun startKoin() {
    startKoin {
        modules(allModules)
    }
}

fun MainViewController() = ComposeUIViewController { App() }