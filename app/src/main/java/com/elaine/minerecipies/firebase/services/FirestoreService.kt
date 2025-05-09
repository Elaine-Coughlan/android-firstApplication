package com.elaine.minerecipies.firebase.services

import com.elaine.minerecipies.data.database.InventoryItem
import kotlinx.coroutines.flow.Flow

typealias InventoryItems = Flow<List<InventoryItem>>

interface FirestoreService {
    suspend fun getAll(email: String): InventoryItems
    suspend fun get(email: String, itemId: String): InventoryItem?
    suspend fun insert(email: String, item: InventoryItem)
    suspend fun update(email: String, item: InventoryItem)
    suspend fun delete(email: String, itemId: String)
    suspend fun countUserRecipes(email: String): Int
    suspend fun countCraftableRecipes(email: String): Int
    suspend fun deleteUserData(email: String)
}