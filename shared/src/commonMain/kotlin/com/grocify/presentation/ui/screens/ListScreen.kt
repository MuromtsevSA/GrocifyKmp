package com.grocify.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grocify.presentation.ui.components.CompletedItemsSection
import com.grocify.presentation.ui.components.ListHeroCard
import com.grocify.presentation.ui.components.PendingItemCard
import com.grocify.presentation.viewmodel.GroceryViewModel

@Composable
fun ListScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val pendingItems = state.items.filter { !it.purchased }

    if (state.isLoading && state.items.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ListHeroCard(insights = state.insights)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "SHOPPING ITEMS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${pendingItems.size} active",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(pendingItems, key = { it.id }) { item ->
            PendingItemCard(
                item = item,
                onTogglePurchased = { viewModel.togglePurchased(item.id) },
                onIncrement = { viewModel.updateQuantity(item.id, item.quantity + 1) },
                onDecrement = {
                    if (item.quantity > 1) {
                        viewModel.updateQuantity(item.id, item.quantity - 1)
                    }
                },
                onDelete = { viewModel.removeItem(item.id) },
            )
        }
        item {
            CompletedItemsSection(
                items = state.items.filter { it.purchased },
                onTogglePurchased = viewModel::togglePurchased,
                onDelete = viewModel::removeItem,
            )
        }
    }
}
