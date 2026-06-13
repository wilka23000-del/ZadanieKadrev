package com.example.zadaniekadrev.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    private val themeKey = booleanPreferencesKey("is_dark_theme")
    private val itemsKey = stringSetPreferencesKey("shopping_items_v2")

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[themeKey] ?: false }
    
    val shoppingItems: Flow<List<ShoppingItem>> = context.dataStore.data.map { preferences ->
        preferences[itemsKey]?.mapNotNull { jsonString ->
            try {
                Json.decodeFromString<ShoppingItem>(jsonString)
            } catch (e: Exception) {
                null
            }
        }?.sortedBy { it.isChecked } ?: emptyList()
    }

    suspend fun setTheme(isDark: Boolean) {
        context.dataStore.edit { it[themeKey] = isDark }
    }

    suspend fun saveItems(items: List<ShoppingItem>) {
        context.dataStore.edit { preferences ->
            preferences[itemsKey] = items.map { Json.encodeToString(it) }.toSet()
        }
    }
}
