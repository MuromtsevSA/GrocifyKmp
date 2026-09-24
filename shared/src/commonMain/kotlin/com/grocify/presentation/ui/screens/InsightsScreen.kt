package com.grocify.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grocify.domain.model.GroceryCategory
import com.grocify.presentation.viewmodel.GroceryViewModel

@Composable
fun InsightsScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val insights = state.insights

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Clerk profile integration coming soon.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Button(
                    onClick = viewModel::signOut,
                    modifier = Modifier.padding(top = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(),
                ) {
                    Text("Sign out")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                InsightRow("Total items", insights.totalItems.toString())
                InsightRow("Pending", insights.pendingCount.toString())
                InsightRow("Completed", insights.completedCount.toString())
                InsightRow("Completion rate", "${insights.completionRate}%")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("By category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (insights.categoryCounts.isEmpty()) {
                    Text("No pending items yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    insights.categoryCounts.forEach { entry ->
                        InsightRow(categoryLabel(entry.key), entry.value.toString())
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Priority", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                InsightRow("High priority pending", insights.highPriorityPending.toString())
            }
        }

        Button(
            onClick = viewModel::clearPurchased,
            modifier = Modifier.fillMaxWidth(),
            enabled = insights.completedCount > 0,
        ) {
            Text("Clear completed items")
        }
    }
}

@Composable
private fun InsightRow(label: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun categoryLabel(category: GroceryCategory): String = when (category) {
    GroceryCategory.Produce -> "Produce"
    GroceryCategory.Dairy -> "Dairy"
    GroceryCategory.Bakery -> "Bakery"
    GroceryCategory.Pantry -> "Pantry"
    GroceryCategory.Snacks -> "Snacks"
}
