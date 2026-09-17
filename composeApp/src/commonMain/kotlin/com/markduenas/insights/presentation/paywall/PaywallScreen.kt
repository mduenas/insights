package com.markduenas.insights.presentation.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.markduenas.insights.billing.BillingProduct
import com.markduenas.insights.billing.FallbackPricing
import com.markduenas.insights.billing.ProductIds

@Composable
fun PaywallContent(
    products: List<BillingProduct>,
    purchaseInProgress: Boolean,
    restoreInProgress: Boolean,
    error: String?,
    onPurchase: (String) -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Kindling Premium",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Unlimited personal insights and cloud backup",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        val features = listOf(
            "Unlimited personal insights (free: 25)",
            "Cloud backup & multi-device sync",
            "Export personal insights as JSON",
            "Support the curated wisdom library",
        )
        features.forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(text = feature, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(24.dp))

        val yearly = products.find { it.id == ProductIds.PREMIUM_YEARLY }
        val monthly = products.find { it.id == ProductIds.PREMIUM_MONTHLY }
        val yearlyPrice = yearly?.formattedPrice?.takeIf { it.isNotBlank() }
            ?: FallbackPricing.YEARLY
        val monthlyPrice = monthly?.formattedPrice?.takeIf { it.isNotBlank() }
            ?: FallbackPricing.MONTHLY

        Button(
            onClick = { onPurchase(ProductIds.PREMIUM_YEARLY) },
            enabled = !purchaseInProgress && !restoreInProgress,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (purchaseInProgress) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Yearly — $yearlyPrice")
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onPurchase(ProductIds.PREMIUM_MONTHLY) },
            enabled = !purchaseInProgress && !restoreInProgress,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Monthly — $monthlyPrice")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = onRestore,
            enabled = !purchaseInProgress && !restoreInProgress,
        ) {
            if (restoreInProgress) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Restore purchases")
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        TextButton(onClick = onDismiss, enabled = !purchaseInProgress) {
            Text("Maybe later")
        }
    }
}
