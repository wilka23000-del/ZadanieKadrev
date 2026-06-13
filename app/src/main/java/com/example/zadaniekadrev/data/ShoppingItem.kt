package com.example.zadaniekadrev.data

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: Int = 1,
    val isChecked: Boolean = false
)
