package com.grocify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grocify.auth.AuthTokenProvider
import com.grocify.domain.model.CreateItemInput
import com.grocify.domain.model.GroceryCategory
import com.grocify.domain.model.GroceryItem
import com.grocify.domain.model.GroceryPriority
import com.grocify.domain.repository.GroceryRepository
import com.grocify.domain.usecase.InsightsCalculator
import com.grocify.domain.usecase.InsightsSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroceryUiState(
    val items: List<GroceryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedIn: Boolean = false,
    val insights: InsightsSnapshot = InsightsCalculator.calculate(emptyList()),
)

class GroceryViewModel(
    private val repository: GroceryRepository,
    private val authTokenProvider: AuthTokenProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroceryUiState())
    val uiState: StateFlow<GroceryUiState> = _uiState.asStateFlow()

    init {
        refreshAuthAndLoad()
    }

    fun refreshAuthAndLoad() {
        viewModelScope.launch {
            val signedIn = authTokenProvider.isSignedIn()
            _uiState.update { it.copy(isSignedIn = signedIn) }
            if (signedIn) {
                loadItems()
            }
        }
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getItems()
                .onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            items = items,
                            isLoading = false,
                            insights = InsightsCalculator.calculate(items),
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Something went wrong",
                        )
                    }
                }
        }
    }

    fun addItem(
        name: String,
        category: GroceryCategory,
        quantity: Int,
        priority: GroceryPriority,
    ) {
        if (name.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            repository.addItem(
                CreateItemInput(
                    name = name.trim(),
                    category = category,
                    quantity = quantity,
                    priority = priority,
                ),
            ).onSuccess { item ->
                _uiState.update { state ->
                    val items = listOf(item) + state.items
                    state.copy(
                        items = items,
                        insights = InsightsCalculator.calculate(items),
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }

    fun updateQuantity(id: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            repository.updateQuantity(id, quantity)
                .onSuccess { updated ->
                    updateItem(updated)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun togglePurchased(id: String) {
        val current = _uiState.value.items.find { it.id == id } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            repository.togglePurchased(id, !current.purchased)
                .onSuccess { updated ->
                    updateItem(updated)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun removeItem(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            repository.removeItem(id)
                .onSuccess {
                    _uiState.update { state ->
                        val items = state.items.filter { it.id != id }
                        state.copy(
                            items = items,
                            insights = InsightsCalculator.calculate(items),
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun clearPurchased() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            repository.clearPurchased()
                .onSuccess {
                    _uiState.update { state ->
                        val items = state.items.filter { !it.purchased }
                        state.copy(
                            items = items,
                            insights = InsightsCalculator.calculate(items),
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authTokenProvider.signOut()
            _uiState.value = GroceryUiState(isSignedIn = false)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun updateItem(updated: GroceryItem) {
        _uiState.update { state ->
            val items = state.items.map { if (it.id == updated.id) updated else it }
            state.copy(
                items = items,
                insights = InsightsCalculator.calculate(items),
            )
        }
    }
}
