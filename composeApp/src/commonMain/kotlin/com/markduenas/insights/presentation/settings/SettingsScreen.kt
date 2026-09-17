package com.markduenas.insights.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.markduenas.insights.presentation.auth.SignInScreen
import com.markduenas.insights.presentation.paywall.PaywallSheetHost

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SettingsScreenModel>()
        val state by screenModel.state.collectAsState()
        val uriHandler = LocalUriHandler.current

        var showDeleteDialog by remember { mutableStateOf(false) }
        var deletePassword by remember { mutableStateOf("") }

        LaunchedEffect(state.didLeaveAccount) {
            if (state.didLeaveAccount) {
                navigator.replaceAll(SignInScreen())
            }
        }

        if (state.showPaywall) {
            PaywallSheetHost(onDismiss = screenModel::dismissPaywall)
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!state.isBusy) {
                        showDeleteDialog = false
                        deletePassword = ""
                        screenModel.clearError()
                    }
                },
                title = { Text("Delete account?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "This permanently deletes your account and all personal insights " +
                                "stored on this device and in the cloud. This cannot be undone.",
                        )
                        OutlinedTextField(
                            value = deletePassword,
                            onValueChange = { deletePassword = it },
                            label = { Text("Confirm with password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            enabled = !state.isBusy,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        state.error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { screenModel.deleteAccount(deletePassword) },
                        enabled = !state.isBusy,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        if (state.isBusy) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Delete forever")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            deletePassword = ""
                            screenModel.clearError()
                        },
                        enabled = !state.isBusy,
                    ) { Text("Cancel") }
                },
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Kindling Premium", style = MaterialTheme.typography.titleMedium)
                    if (state.isPremium) {
                        Text(
                            "Premium active — unlimited insights & cloud backup.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        OutlinedButton(
                            onClick = screenModel::exportPersonalInsights,
                            enabled = !state.isBusy,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Export personal insights (JSON)")
                        }
                        state.exportMessage?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        Text(
                            "Free plan: ${state.personalCount}/${SettingsScreenModel.FREE_LIMIT} personal insights. Upgrade for unlimited + cloud sync.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = screenModel::showPaywall,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Upgrade to Premium")
                        }
                    }
                    TextButton(
                        onClick = screenModel::restorePurchases,
                        enabled = !state.restoreInProgress && !state.isBusy,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (state.restoreInProgress) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Restore purchases")
                        }
                    }

                    if (state.showDebugPremiumToggle) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        Text("Developer (debug only)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Bypass store purchases for QA. Not shown in release builds.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Grant Premium", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = state.debugPremiumOverride,
                                onCheckedChange = screenModel::setDebugPremiumOverride,
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))

                    Text("Account", style = MaterialTheme.typography.titleMedium)

                    if (state.isSignedIn) {
                        Text(
                            state.email ?: "Signed in",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (state.error != null && !showDeleteDialog) {
                            Text(
                                state.error ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        OutlinedButton(
                            onClick = screenModel::signOut,
                            enabled = !state.isBusy,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Sign out")
                        }
                        Button(
                            onClick = {
                                screenModel.clearError()
                                deletePassword = ""
                                showDeleteDialog = true
                            },
                            enabled = !state.isBusy,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Delete account")
                        }
                    } else {
                        Text(
                            "You are browsing without an account. Sign in to save personal insights.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = { navigator.replaceAll(SignInScreen()) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Sign in")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))

                    Text("Legal & support", style = MaterialTheme.typography.titleMedium)

                    TextButton(
                        onClick = { uriHandler.openUri(SettingsScreenModel.PRIVACY_URL) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Privacy policy", modifier = Modifier.weight(1f))
                        Icon(Icons.Default.OpenInNew, contentDescription = null, Modifier.size(18.dp))
                    }
                    TextButton(
                        onClick = { uriHandler.openUri(SettingsScreenModel.SUPPORT_URL) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Support / website", modifier = Modifier.weight(1f))
                        Icon(Icons.Default.OpenInNew, contentDescription = null, Modifier.size(18.dp))
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Kindling — curated wisdom, personal capture.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
