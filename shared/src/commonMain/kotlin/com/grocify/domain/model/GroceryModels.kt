package com.grocify.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GroceryCategory {
    @SerialName("Produce") Produce,
    @SerialName("Dairy") Dairy,
    @SerialName("Bakery") Bakery,
    @SerialName("Pantry") Pantry,
    @SerialName("Snacks") Snacks,
}

@Serializable
enum class GroceryPriority {
    @SerialName("low") Low,
    @SerialName("medium") Medium,
    @SerialName("high") High,
}

@Serializable
data class GroceryItem(
    val id: String,
    val name: String,
    val category: GroceryCategory,
    val quantity: Int,
    val purchased: Boolean,
    val priority: GroceryPriority,
)

@Serializable
data class CreateItemInput(
    val name: String,
    val category: GroceryCategory,
    val quantity: Int,
    val priority: GroceryPriority,
)

@Serializable
data class ItemsResponse(
    val items: List<GroceryItem> = emptyList(),
)

@Serializable
data class ItemResponse(
    val item: GroceryItem,
)

@Serializable
data class ApiErrorResponse(
    val error: String? = null,
)
