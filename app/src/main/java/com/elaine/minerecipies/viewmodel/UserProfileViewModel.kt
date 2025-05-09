package com.elaine.minerecipies.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.firebase.auth.Response
import com.elaine.minerecipies.firebase.models.FirebaseUser
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.FirestoreService
import com.elaine.minerecipies.firebase.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirestoreService,
    private val storageService: StorageService
) : ViewModel() {

    private val _userProfile = MutableStateFlow<FirebaseUser?>(null)
    val userProfile: StateFlow<FirebaseUser?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Stats
    private val _inventoryCount = MutableStateFlow(0)
    val inventoryCount: StateFlow<Int> = _inventoryCount

    private val _recipesCount = MutableStateFlow(0)
    val recipesCount: StateFlow<Int> = _recipesCount

    private val _craftableCount = MutableStateFlow(0)
    val craftableCount: StateFlow<Int> = _craftableCount

    private val _isAuthenticated = MutableStateFlow(authService.isUserAuthenticatedInFirebase)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _displayName = MutableStateFlow(authService.displayName ?: "Your")
    val displayName: StateFlow<String> = _displayName

    // Dialog states
    var showDeleteDialog by mutableStateOf(false)
        private set

    var showEditNameDialog by mutableStateOf(false)
        private set

    var showResetPwdDialog by mutableStateOf(false)
        private set

    init {
        if (authService.isUserAuthenticatedInFirebase) {
            loadUserProfile()
            loadUserStats()
        }

        viewModelScope.launch {
            authService.authStateFlow.collect { isAuth ->
                _isAuthenticated.value = isAuth
                if (isAuth) {
                    _displayName.value = authService.displayName ?: "Your"
                } else {
                    _displayName.value = "Your"
                }
            }
        }
    }

    // Add this method to update inventory count from outside
    fun updateInventoryCount(count: Int) {
        _inventoryCount.value = count
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val email = authService.email
                if (email != null) {
                    // Fetch user profile
                    _userProfile.value = FirebaseUser(
                        uid = authService.currentUserId,
                        displayName = authService.displayName ?: "",
                        email = email
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                val email = authService.email ?: return@launch

                // We now get inventory count from the UI layer
                // No need to access inventoryViewModel here

                // Get other stats from Firestore
                _recipesCount.value = firestoreService.countUserRecipes(email)
                _craftableCount.value = firestoreService.countCraftableRecipes(email)
            } catch (e: Exception) {
                // Just log error, don't show to user as this is supplementary info
                println("Error loading stats: ${e.message}")
            }
        }
    }

    // Rest of your methods remain unchanged
    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authService.updateProfile(displayName)
                loadUserProfile() // Reload the profile to reflect changes
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfilePhoto(photoUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val downloadUrl = storageService.uploadProfilePhoto(photoUri)
                authService.updateProfilePhoto(downloadUrl)
                loadUserProfile()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update photo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authService.resetPassword(email)
                if (result is Response.Success) {
                    _errorMessage.value = "Password reset email sent to $email"
                } else if (result is Response.Failure) {
                    _errorMessage.value = "Failed to send reset email: ${result.e.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Delete user's inventory
                val email = authService.email
                if (email != null) {
                    firestoreService.deleteUserData(email)
                }

                // Delete profile photo
                storageService.deleteProfilePhoto()

                // Delete the Firebase Auth account
                authService.deleteAccount()

                // Sign out
                authService.signOut()

                hideDeleteAccountDialog()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete account: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
        }
    }

    // Dialog management
    fun showDeleteAccountDialog() {
        showDeleteDialog = true
    }

    fun hideDeleteAccountDialog() {
        showDeleteDialog = false
    }

    fun showEditDisplayNameDialog() {
        showEditNameDialog = true
    }

    fun hideEditDisplayNameDialog() {
        showEditNameDialog = false
    }

    fun showResetPasswordDialog() {
        showResetPwdDialog = true
    }

    fun hideResetPasswordDialog() {
        showResetPwdDialog = false
    }

    fun clearError() {
        _errorMessage.value = null
    }

}