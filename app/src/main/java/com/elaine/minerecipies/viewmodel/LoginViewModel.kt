package com.elaine.minerecipies.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.firebase.auth.Response
import com.elaine.minerecipies.firebase.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authService.authenticateUser(email, password)
                if (result is Response.Success) {
                    _isAuthenticated.value = true
                    onSuccess()
                } else if (result is Response.Failure) {
                    _errorMessage.value = result.e.message ?: "Authentication failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return authService.getGoogleSignInIntent()
    }

    fun handleGoogleSignInResult(data: Intent?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authService.handleGoogleSignInResult(data)

            when (result) {
                is Response.Success -> {
                    onSuccess()
                }
                is Response.Failure -> {
                    _errorMessage.value = result.e.message ?: "Google sign-in failed"
                }
                is Response.Loading -> {
                    // Do nothing, already in loading state
                }
            }

            _isLoading.value = false
        }
    }
}