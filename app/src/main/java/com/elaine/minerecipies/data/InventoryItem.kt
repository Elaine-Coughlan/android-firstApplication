package com.elaine.minerecipies.data


data class InventoryItem(
    val id: Int = 0, // Auto-generated ID for Room
    val name: String,  // Name of the item/block
    val quantity: Int, // Quantity in inventory
    val type: String // "Item" or "Block"
)
