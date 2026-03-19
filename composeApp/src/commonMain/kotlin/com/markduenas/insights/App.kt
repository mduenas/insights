package com.markduenas.insights

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.markduenas.insights.presentation.auth.SignInScreen
import com.markduenas.insights.presentation.home.HomeScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(SignInScreen())
    }
}
