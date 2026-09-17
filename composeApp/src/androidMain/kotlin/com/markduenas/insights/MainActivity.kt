package com.markduenas.insights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.markduenas.insights.data.BillingRepository
import com.markduenas.insights.di.allModules
import com.markduenas.insights.domain.CloudSyncCoordinator
import com.markduenas.insights.platform.ActivityProvider
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(allModules)
            }
        }

        ActivityProvider.activity = this
        get<BillingRepository>().initialize()
        get<CloudSyncCoordinator>().syncIfEligible()

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        ActivityProvider.activity = this
    }

    override fun onDestroy() {
        if (ActivityProvider.activity === this) {
            ActivityProvider.activity = null
        }
        super.onDestroy()
    }
}
