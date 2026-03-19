package com.markduenas.insights.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.markduenas.insights.presentation.detail.InsightDetailScreenModel
import org.koin.core.parameter.parametersOf

data class AdminDetailScreen(val insightId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // Reuse InsightDetailScreenModel pointing at pending_insights collection via same ID
        val screenModel = getScreenModel<AdminScreenModel>()
        val state by screenModel.state.collectAsState()
        val insight = state.pendingInsights.find { it.id == insightId }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Review Insight") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (insight == null) {
                    Text("Insight not found or already processed.",
                        style = MaterialTheme.typography.bodyMedium)
                    return@Column
                }

                Text(insight.title, style = MaterialTheme.typography.headlineSmall)
                Text(insight.body, style = MaterialTheme.typography.bodyMedium)

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Source", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text(insight.source.title, style = MaterialTheme.typography.bodyMedium)
                        insight.source.author?.let { Text("by $it") }
                        insight.source.year?.let { Text("$it") }
                        insight.source.url?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    }
                }

                if (insight.tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        insight.tags.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { screenModel.reject(insightId); navigator.pop() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Reject") }
                    Button(
                        onClick = { screenModel.approve(insightId); navigator.pop() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Approve") }
                }
            }
        }
    }
}
