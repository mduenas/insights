package com.markduenas.insights.presentation.paywall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

/**
 * Hosts the premium paywall as a modal bottom sheet, wired to [PaywallScreenModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallSheetHost(
    onDismiss: () -> Unit,
) {
    val screenModel = koinInject<PaywallScreenModel>()
    val state by screenModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.didPurchaseOrRestore) {
        if (state.didPurchaseOrRestore) {
            screenModel.consumeSuccess()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            PaywallContent(
                products = state.products,
                purchaseInProgress = state.purchaseInProgress,
                restoreInProgress = state.restoreInProgress,
                error = state.error,
                onPurchase = screenModel::purchase,
                onRestore = screenModel::restore,
                onDismiss = onDismiss,
            )
        }
    }
}
