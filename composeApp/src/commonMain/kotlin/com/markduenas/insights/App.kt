package com.markduenas.insights

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.markduenas.insights.presentation.auth.SignInScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

@Composable
fun App() {
    // Initialize Firebase Analytics for crash/event tracking in alpha
    val analytics = remember { Firebase.analytics }

    MaterialTheme {
        Navigator(SignInScreen())
    }
}
