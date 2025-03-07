package com.elaine.minerecipies.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE name = :name LIMIT 1")
    suspend fun getItemByName(name: String): InventoryItem?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)
}