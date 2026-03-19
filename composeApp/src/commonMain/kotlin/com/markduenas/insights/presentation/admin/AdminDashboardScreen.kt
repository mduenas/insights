package com.markduenas.insights.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.markduenas.insights.domain.model.Insight

class AdminDashboardScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AdminScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin — Pending Insights") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            when {
                state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.pendingInsights.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Text("No pending insights. ✓", style = MaterialTheme.typography.bodyMedium)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.pendingInsights, key = { it.id }) { insight ->
                        PendingInsightCard(
                            insight = insight,
                            onApprove = { screenModel.approve(insight.id) },
                            onReject = { screenModel.reject(insight.id) },
                            onDetail = { navigator.push(AdminDetailScreen(insight.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingInsightCard(
    insight: Insight,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDetail: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(insight.title, style = MaterialTheme.typography.titleMedium)
            Text(insight.body.take(120) + if (insight.body.length > 120) "…" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("— ${insight.source.title}${insight.source.author?.let { ", $it" } ?: ""}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDetail, modifier = Modifier.weight(1f)) { Text("Review") }
                FilledTonalButton(onClick = onApprove,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Icon(Icons.Default.Check, "Approve", modifier = Modifier.size(18.dp))
                }
                FilledTonalButton(onClick = onReject,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Icon(Icons.Default.Close, "Reject", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
