package com.example.zadaniekadrev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zadaniekadrev.data.SettingsManager
import com.example.zadaniekadrev.data.ShoppingItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val settingsManager: SettingsManager) : ViewModel() {
    val isDarkTheme: StateFlow<Boolean> = settingsManager.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val items: StateFlow<List<ShoppingItem>> = settingsManager.shoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleTheme() {
        viewModelScope.launch {
            settingsManager.setTheme(!isDarkTheme.value)
        }
    }

    fun addItem(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newItem = ShoppingItem(id = UUID.randomUUID().toString(), name = name)
            val current = items.value.toMutableList()
            current.add(newItem)
            settingsManager.saveItems(current)
        }
    }

    fun removeItem(item: ShoppingItem) {
        viewModelScope.launch {
            val current = items.value.filter { it.id != item.id }
            settingsManager.saveItems(current)
        }
    }

    fun toggleItemChecked(item: ShoppingItem) {
        viewModelScope.launch {
            val current = items.value.map {
                if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it
            }
            settingsManager.saveItems(current)
        }
    }

    fun updateQuantity(item: ShoppingItem, delta: Int) {
        viewModelScope.launch {
            val current = items.value.map {
                if (it.id == item.id) {
                    val newQty = (it.quantity + delta).coerceAtLeast(1)
                    it.copy(quantity = newQty)
                } else it
            }
            settingsManager.saveItems(current)
        }
    }
}
