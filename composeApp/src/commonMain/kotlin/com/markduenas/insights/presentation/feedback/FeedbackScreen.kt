package com.markduenas.insights.presentation.feedback

import androidx.compose.foundation.layout.*
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

class FeedbackScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<FeedbackScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (state.mode == FeedbackMode.TOPIC) "Request a Topic" else "Send Feedback") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                if (state.isSubmitted) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🙏", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(12.dp))
                        Text("Thank you!", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (state.mode == FeedbackMode.TOPIC)
                                "Your topic request helps shape what's coming next."
                            else
                                "Your input helps shape the app.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { navigator.pop() }) {
                            Text("Back to Insights")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = state.mode == FeedbackMode.FEEDBACK,
                                onClick = { screenModel.onModeChange(FeedbackMode.FEEDBACK) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                label = { Text("Feedback") }
                            )
                            SegmentedButton(
                                selected = state.mode == FeedbackMode.TOPIC,
                                onClick = { screenModel.onModeChange(FeedbackMode.TOPIC) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                label = { Text("Request Topic") }
                            )
                        }

                        Text(
                            if (state.mode == FeedbackMode.TOPIC)
                                "What topics or categories would you like to see more insights on?"
                            else
                                "We'd love to hear what's working, what's missing, or any ideas you have.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = state.message,
                            onValueChange = screenModel::onMessageChange,
                            label = { Text(if (state.mode == FeedbackMode.TOPIC) "Topic or category" else "Your feedback") },
                            placeholder = {
                                Text(
                                    if (state.mode == FeedbackMode.TOPIC)
                                        "e.g. stoicism, parenting, creativity…"
                                    else
                                        "Share your thoughts…"
                                )
                            },
                            minLines = 5,
                            maxLines = 10,
                            modifier = Modifier.fillMaxWidth()
                        )

                        state.error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = { screenModel.submit() },
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            else Text(if (state.mode == FeedbackMode.TOPIC) "Submit Request" else "Submit Feedback")
                        }
                    }
                }
            }
        }
    }
}
