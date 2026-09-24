package com.grocify.data.local

import com.grocify.domain.model.GroceryCategory
import com.grocify.domain.model.GroceryItem
import com.grocify.domain.model.GroceryPriority

internal fun Grocery_item.toDomain(): GroceryItem =
    GroceryItem(
        id = id,
        name = name,
        category = GroceryCategory.valueOf(category),
        quantity = quantity.toInt(),
        purchased = purchased == 1L,
        priority = when (priority.lowercase()) {
            "low" -> GroceryPriority.Low
            "high" -> GroceryPriority.High
            else -> GroceryPriority.Medium
        },
    )

internal fun GroceryPriority.toStorageValue(): String =
    when (this) {
        GroceryPriority.Low -> "low"
        GroceryPriority.Medium -> "medium"
        GroceryPriority.High -> "high"
    }
