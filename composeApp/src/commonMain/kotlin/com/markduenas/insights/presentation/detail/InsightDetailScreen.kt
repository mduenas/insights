package com.markduenas.insights.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf

data class InsightDetailScreen(val insightId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsightDetailScreenModel> { parametersOf(insightId) }
        val insight by screenModel.insight.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(insight?.title ?: "Insight") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            when (val i = insight) {
                null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(i.title, style = MaterialTheme.typography.headlineSmall)
                    Text(i.body, style = MaterialTheme.typography.bodyMedium)

                    // Source card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Source", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                            Text(i.source.title, style = MaterialTheme.typography.bodyMedium)
                            i.source.author?.let { Text("by $it", style = MaterialTheme.typography.bodySmall) }
                            i.source.year?.let { Text("$it", style = MaterialTheme.typography.bodySmall) }
                            i.source.url?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Tags
                    if (i.tags.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            i.tags.forEach { tag -> AssistChip(onClick = {}, label = { Text(tag) }) }
                        }
                    }

                    // Linked common insight badge
                    if (i.linkedCommonInsightId != null) {
                        Card(colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                            Text(
                                "Linked to a common insight",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
