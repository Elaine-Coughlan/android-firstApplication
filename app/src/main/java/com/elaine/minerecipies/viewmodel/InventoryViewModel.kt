package com.elaine.minerecipies.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.data.Blocks.Blocks
import com.elaine.minerecipies.data.InventoryItem
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.util.loadItems
import com.elaine.minerecipies.util.loadBlocks
import com.elaine.minerecipies.util.loadRecipes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(context: Context) : ViewModel() {

    private val _inventoryList = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryList: StateFlow<List<InventoryItem>> = _inventoryList

    val availableItems: List<Items> = loadItems(context)
    val availableBlocks: List<Blocks> = loadBlocks(context)

    init {
        viewModelScope.launch {

        }
    }

    fun addToInventory(name: String, quantity: Int, type: String) {
        val existingItem = _inventoryList.value.find { it.name == name }

        _inventoryList.value = if (existingItem != null) {
            _inventoryList.value.map {
                if (it.name == name) it.copy(quantity = it.quantity + quantity) else it
            }
        } else {
            _inventoryList.value + InventoryItem(name = name, quantity = quantity, type = type)
        }
    }

    fun removeFromInventory(item: InventoryItem) {
        _inventoryList.value = _inventoryList.value.filter { it.name != item.name }
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
