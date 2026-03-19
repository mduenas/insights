package com.markduenas.insights.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.markduenas.insights.presentation.detail.InsightDetailScreen
import com.markduenas.insights.presentation.feedback.FeedbackScreen
import com.markduenas.insights.presentation.personal.PersonalInsightsScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Insights") },
                    actions = {
                        IconButton(onClick = { navigator.push(FeedbackScreen()) }) {
                            Icon(Icons.Default.Feedback, contentDescription = "Send Feedback")
                        }
                        IconButton(onClick = { navigator.push(PersonalInsightsScreen()) }) {
                            Icon(Icons.Default.Person, contentDescription = "My Insights")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = screenModel::onSearchQueryChange,
                    placeholder = { Text("Search insights…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                when {
                    state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                    }
                    state.insights.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No insights yet.", style = MaterialTheme.typography.bodyMedium)
                    }
                    else -> LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.insights, key = { it.id }) { insight ->
                            InsightCard(insight) { navigator.push(InsightDetailScreen(insight.id)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: Insight, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(insight.title, style = MaterialTheme.typography.titleMedium)
            Text(
                insight.body.take(120) + if (insight.body.length > 120) "…" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "— ${insight.source.title}${insight.source.author?.let { ", $it" } ?: ""}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            if (insight.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    insight.tags.take(3).forEach { tag ->
                        AssistChip(onClick = {}, label = { Text(tag) })
                    }
                }
            }
        }
    }
}
