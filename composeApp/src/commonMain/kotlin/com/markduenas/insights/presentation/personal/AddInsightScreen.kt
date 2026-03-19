package com.markduenas.insights.presentation.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.markduenas.insights.domain.model.Insight

class AddInsightScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AddInsightScreenModel>()
        val state by screenModel.state.collectAsState()

        // Navigate back once saved (and no match dialog pending)
        LaunchedEffect(state.savedInsight, state.showMatchDialog) {
            if (state.savedInsight != null && !state.showMatchDialog) navigator.pop()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Insight") },
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(state.title, screenModel::onTitleChange,
                    label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(state.body, screenModel::onBodyChange,
                    label = { Text("Body *") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                Text("Source", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(state.sourceTitle, screenModel::onSourceTitleChange,
                    label = { Text("Source Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(state.sourceAuthor, screenModel::onSourceAuthorChange,
                    label = { Text("Author") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(state.sourceUrl, screenModel::onSourceUrlChange,
                    label = { Text("URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(state.sourceYear, screenModel::onSourceYearChange,
                    label = { Text("Year") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                OutlinedTextField(state.tags, screenModel::onTagsChange,
                    label = { Text("Tags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)

                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = screenModel::save,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Save Insight")
                }
            }
        }

        // Match suggestions dialog
        if (state.showMatchDialog) {
            MatchSuggestionsDialog(
                matches = state.matchSuggestions,
                onLink = { commonInsightId ->
                    state.savedInsight?.id?.let { screenModel.linkToCommonInsight(it, commonInsightId) }
                },
                onDismiss = screenModel::dismissMatchDialog
            )
        }
    }
}

@Composable
private fun MatchSuggestionsDialog(
    matches: List<Insight>,
    onLink: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Similar Insights Found") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your insight matches these common insights. Link to one?",
                    style = MaterialTheme.typography.bodySmall)
                matches.forEach { match ->
                    OutlinedButton(onClick = { onLink(match.id) },
                        modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(match.title, style = MaterialTheme.typography.bodyMedium)
                            Text(match.source.title, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Skip") } }
    )
}
