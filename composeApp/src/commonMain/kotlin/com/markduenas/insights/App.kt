package com.markduenas.insights

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.markduenas.insights.presentation.auth.SignInScreen
import com.markduenas.insights.ui.InsightsTheme
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.crashlytics.crashlytics

@Composable
fun App() {
    remember {
        Firebase.analytics
        Firebase.crashlytics.apply { setCrashlyticsCollectionEnabled(true) }
    }

    InsightsTheme {
        Navigator(SignInScreen())
    }
}
