package com.markduenas.insights.presentation.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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

class PersonalInsightsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<PersonalInsightsScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Insights") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { navigator.push(AddInsightScreen()) }) {
                    Icon(Icons.Default.Add, "Add Insight")
                }
            }
        ) { padding ->
            when {
                state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.insights.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("No personal insights yet.", style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { navigator.push(AddInsightScreen()) }) { Text("Add your first insight") }
                    }
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.insights, key = { it.id }) { insight ->
                        PersonalInsightCard(insight, onDelete = { screenModel.delete(insight.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalInsightCard(insight: Insight, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Text(insight.title, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f))
                IconButton(onClick = { showConfirm = true }) {
                    Icon(Icons.Default.Delete, "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(insight.body.take(100) + if (insight.body.length > 100) "…" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("— ${insight.source.title}", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
            if (insight.linkedCommonInsightId != null) {
                Text("🔗 Linked to common insight",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Delete insight?") },
            text = { Text("This will remove \"${insight.title}\" from your device.") },
            confirmButton = { TextButton(onClick = { onDelete(); showConfirm = false }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }
}
