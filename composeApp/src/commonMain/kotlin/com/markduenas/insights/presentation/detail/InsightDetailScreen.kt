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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    title = {},
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
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Title
                    Text(
                        i.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 36.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

                    // Decorative quote mark + body
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "\u201C",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            lineHeight = 8.sp,
                            modifier = Modifier.height(32.dp)
                        )
                        Text(
                            i.body,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Source attribution
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        val authorLine = buildString {
                            append("— ${i.source.title}")
                            i.source.author?.let { append(", $it") }
                        }
                        Text(
                            authorLine,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                        i.source.year?.let { year ->
                            if (year != 0) Text(
                                if (year < 0) "${-year} BC" else "$year AD",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        i.source.url?.let { url ->
                            Text(
                                url,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Tags
                    if (i.tags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            i.tags.forEach { tag ->
                                SuggestionChip(onClick = {}, label = { Text(tag) })
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
