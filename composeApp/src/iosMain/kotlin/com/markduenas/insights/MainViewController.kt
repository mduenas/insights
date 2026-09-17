package com.markduenas.insights

import androidx.compose.ui.window.ComposeUIViewController
import com.markduenas.insights.data.BillingRepository
import com.markduenas.insights.di.allModules
import com.markduenas.insights.domain.CloudSyncCoordinator
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin
import org.koin.mp.KoinPlatformTools

fun startKoin() {
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        startKoin {
            modules(allModules)
        }
    }
    runCatching {
        getKoin().get<BillingRepository>().initialize()
        getKoin().get<CloudSyncCoordinator>().syncIfEligible()
    }
}

fun MainViewController() = ComposeUIViewController { App() }
