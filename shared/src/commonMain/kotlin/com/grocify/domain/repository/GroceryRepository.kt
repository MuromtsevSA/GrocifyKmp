package com.grocify.domain.repository

import com.grocify.domain.model.CreateItemInput
import com.grocify.domain.model.GroceryItem

interface GroceryRepository {
    suspend fun getItems(): Result<List<GroceryItem>>
    suspend fun addItem(input: CreateItemInput): Result<GroceryItem>
    suspend fun updateQuantity(id: String, quantity: Int): Result<GroceryItem>
    suspend fun togglePurchased(id: String, purchased: Boolean): Result<GroceryItem>
    suspend fun removeItem(id: String): Result<Unit>
    suspend fun clearPurchased(): Result<Unit>
}
