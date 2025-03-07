package com.elaine.minerecipies.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.data.Blocks.Blocks
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.database.InventoryDatabase
import com.elaine.minerecipies.data.api.loadItems
import com.elaine.minerecipies.data.api.loadBlocks
import com.elaine.minerecipies.data.database.InventoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryViewModel(context: Context) : ViewModel() {

    private val inventoryDao = InventoryDatabase.getDatabase(context).inventoryDao()

    private val _inventoryList = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryList: StateFlow<List<InventoryItem>> = _inventoryList

    val availableItems: List<Items> = loadItems(context)
    val availableBlocks: List<Blocks> = loadBlocks(context)

    init {
        viewModelScope.launch {
            inventoryDao.getAllItems().collect {
                _inventoryList.value = it
            }
        }
    }

    fun addToInventory(name: String, quantity: Int, type: String) {
        viewModelScope.launch {
            val existingItem = inventoryDao.getItemByName(name)
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                inventoryDao.insertItem(updatedItem)
            } else {
                inventoryDao.insertItem(InventoryItem(name = name, quantity = quantity, type = type))
            }
        }
    }

    fun removeFromInventory(item: InventoryItem) {
        viewModelScope.launch {
            inventoryDao.deleteItem(item)
        }
    }

    class InventoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                return InventoryViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
