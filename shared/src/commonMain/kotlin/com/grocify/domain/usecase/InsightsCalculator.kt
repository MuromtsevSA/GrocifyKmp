package com.grocify.domain.usecase

import com.grocify.domain.model.GroceryCategory
import com.grocify.domain.model.GroceryItem
import com.grocify.domain.model.GroceryPriority

data class InsightsSnapshot(
    val totalItems: Int,
    val pendingCount: Int,
    val completedCount: Int,
    val completionRate: Int,
    val highPriorityPending: Int,
    val totalPendingQuantity: Int,
    val categoryCounts: Map<GroceryCategory, Int>,
)

object InsightsCalculator {
    fun calculate(items: List<GroceryItem>): InsightsSnapshot {
        val pending = items.filter { !it.purchased }
        val completed = items.filter { it.purchased }
        val total = items.size
        val completionRate = if (total == 0) 0 else ((completed.size * 100f) / total).toInt()

        val categoryCounts = GroceryCategory.entries.associateWith { category ->
            pending.count { it.category == category }
        }.filterValues { it > 0 }

        return InsightsSnapshot(
            totalItems = total,
            pendingCount = pending.size,
            completedCount = completed.size,
            completionRate = completionRate,
            highPriorityPending = pending.count { it.priority == GroceryPriority.High },
            totalPendingQuantity = pending.sumOf { it.quantity },
            categoryCounts = categoryCounts,
        )
    }
}
