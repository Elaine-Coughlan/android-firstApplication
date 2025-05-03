package com.elaine.minerecipies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.data.Blocks.Blocks
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.api.loadBlocks
import com.elaine.minerecipies.data.api.loadItems
import com.elaine.minerecipies.data.database.InventoryDao
import com.elaine.minerecipies.data.database.InventoryItem
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class InventoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inventoryDao: InventoryDao,
    private val authService: AuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    // State flow for inventory list
    private val _inventoryList = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryList: StateFlow<List<InventoryItem>> = _inventoryList

    // Error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Available items and blocks
    val availableItems: List<Items> = loadItems(context)
    val availableBlocks: List<Blocks> = loadBlocks(context)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        loadInventory()
        _isLoggedIn.value = authService.isUserAuthenticatedInFirebase
    }

    private fun loadInventory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (authService.isUserAuthenticatedInFirebase) {
                    // If signed in, load from Firestore
                    loadFromFirestore()
                } else {
                    // If not signed in, load from local database
                    loadFromLocalDatabase()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load inventory: ${e.message}"
                // Fall back to local database
                loadFromLocalDatabase()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFromFirestore() {
        viewModelScope.launch {
            try {
                val email = authService.email
                if (email != null) {
                    firestoreService.getAll(email).collect { items ->
                        _inventoryList.value = items

                        // Also update the local database
                        items.forEach { item ->
                            inventoryDao.insertItem(item)
                        }
                    }
                } else {
                    _errorMessage.value = "User email not available"
                    loadFromLocalDatabase()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load from Firestore: ${e.message}"
                loadFromLocalDatabase()
            }
        }
    }

    private fun loadFromLocalDatabase() {
        viewModelScope.launch {
            inventoryDao.getAllItems().collect { items ->
                _inventoryList.value = items
            }
        }
    }

    fun addItem(name: String, quantity: Int, type: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First check if item already exists
                val existingItem = inventoryDao.getItemByName(name)

                if (existingItem != null) {
                    // Update existing item
                    val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                    inventoryDao.insertItem(updatedItem)

                    // If signed in, also update Firestore
                    if (authService.isUserAuthenticatedInFirebase) {
                        val email = authService.email
                        if (email != null) {
                            firestoreService.update(email, updatedItem)
                        }
                    }
                } else {
                    // Add new item
                    val newItem = InventoryItem(
                        name = name,
                        quantity = quantity,
                        type = type
                    )

                    inventoryDao.insertItem(newItem)

                    // If signed in, also add to Firestore
                    if (authService.isUserAuthenticatedInFirebase) {
                        val email = authService.email
                        if (email != null) {
                            firestoreService.insert(email, newItem)
                        }
                    }
                }

                // Refresh the inventory list
                loadInventory()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to add item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeItem(item: InventoryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Remove from local database
                inventoryDao.deleteItem(item)

                // If signed in, also remove from Firestore
                if (authService.isUserAuthenticatedInFirebase) {
                    val email = authService.email
                    if (email != null) {
                        firestoreService.delete(email, item.id.toString())
                    }
                }

                // Refresh the inventory list
                loadInventory()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
            _isLoggedIn.value = false
        }
    }
}