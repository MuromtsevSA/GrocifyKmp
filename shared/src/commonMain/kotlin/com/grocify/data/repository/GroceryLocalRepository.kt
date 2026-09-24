package com.grocify.data.repository

import com.grocify.auth.CurrentUserProvider
import com.grocify.data.local.GrocifyDatabase
import com.grocify.data.local.toDomain
import com.grocify.data.local.toStorageValue
import com.grocify.domain.model.CreateItemInput
import com.grocify.domain.model.GroceryItem
import com.grocify.domain.repository.GroceryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GroceryLocalRepository(
    private val database: GrocifyDatabase,
    private val currentUser: CurrentUserProvider,
) : GroceryRepository {

    private val queries get() = database.groceryQueries

    override suspend fun getItems(): Result<List<GroceryItem>> = withUser { userId ->
        queries.selectAllForUser(userId)
            .executeAsList()
            .map { it.toDomain() }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun addItem(input: CreateItemInput): Result<GroceryItem> = withUser { userId ->
        val now = Clock.System.now().toEpochMilliseconds()
        val item = GroceryItem(
            id = Uuid.random().toString(),
            name = input.name.trim(),
            category = input.category,
            quantity = maxOf(1, input.quantity),
            purchased = false,
            priority = input.priority,
        )
        queries.insertItem(
            id = item.id,
            user_id = userId,
            name = item.name,
            category = item.category.name,
            quantity = item.quantity.toLong(),
            purchased = 0,
            priority = item.priority.toStorageValue(),
            updated_at = now,
        )
        item
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateQuantity(id: String, quantity: Int): Result<GroceryItem> =
        withUser { userId ->
            val now = Clock.System.now().toEpochMilliseconds()
            val safeQuantity = maxOf(1, quantity).toLong()
            queries.updateQuantity(safeQuantity, now, id, userId)
            queries.selectById(id, userId).executeAsOneOrNull()?.toDomain()
                ?: throw NoSuchElementException("Item not found")
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun togglePurchased(id: String, purchased: Boolean): Result<GroceryItem> =
        withUser { userId ->
            val now = Clock.System.now().toEpochMilliseconds()
            queries.updatePurchased(if (purchased) 1 else 0, now, id, userId)
            queries.selectById(id, userId).executeAsOneOrNull()?.toDomain()
                ?: throw NoSuchElementException("Item not found")
        }

    override suspend fun removeItem(id: String): Result<Unit> = withUser { userId ->
        queries.deleteItem(id, userId)
    }

    override suspend fun clearPurchased(): Result<Unit> = withUser { userId ->
        queries.clearPurchasedForUser(userId)
    }

    private suspend fun <T> withUser(block: suspend (String) -> T): Result<T> =
        withContext(Dispatchers.IO) {
            runCatching {
                val userId = currentUser.getUserId() ?: throw SignInRequiredException()
                block(userId)
            }.mapLocalError()
        }

    private fun <T> Result<T>.mapLocalError(): Result<T> = fold(
        onSuccess = { Result.success(it) },
        onFailure = { error ->
            val message = when (error) {
                is SignInRequiredException -> error.message ?: "Please sign in again"
                is NoSuchElementException -> "Item not found"
                else -> error.message ?: "Something went wrong"
            }
            Result.failure(Exception(message, error))
        },
    )
}

class SignInRequiredException : Exception("Please sign in again")
