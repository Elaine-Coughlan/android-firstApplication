package com.elaine.minerecipies.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.firebase.models.FirebaseUser
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _userProfile = MutableStateFlow<FirebaseUser?>(null)
    val userProfile: StateFlow<FirebaseUser?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    var showDeleteDialog by mutableStateOf(false)
        private set

    init {
        if (authService.isUserAuthenticatedInFirebase) {
            loadUserProfile()
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val email = authService.email
                if (email != null) {
                    // Fetch user profile from Firestore
                    // This implementation will depend on your FirestoreService structure
                    // You might need to add a method like getUserProfile(email)
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

    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Implementation will depend on your AuthService
                // You might need to add a method like updateProfile(displayName)
                authService.createUser(displayName, authService.email ?: "", "")
                loadUserProfile()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Implementation will depend on your AuthService
                // Delete user data from Firestore first
                val email = authService.email
                if (email != null) {
                    // Delete user's inventory
                    // This is a conceptual implementation - adjust according to your actual service methods
                    firestoreService.delete(email, "")
                }

                // Then delete the Firebase Auth account
                // You might need to add a method like deleteAccount() to AuthService

                // Sign out
                authService.signOut()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete account: ${e.message}"
            } finally {
                _isLoading.value = false
                hideDeleteAccountDialog()
            }
        }
    }

    fun showDeleteAccountDialog() {
        showDeleteDialog = true
    }

    fun hideDeleteAccountDialog() {
        showDeleteDialog = false
    }
}