package com.elaine.minerecipies.viewmodel

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

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authService.authenticateUser(email, password)

            when (result) {
                is Response.Success -> {
                    // Call the callback to signal success
                    onSuccess()
                }
                is Response.Failure -> {
                    _errorMessage.value = result.e.message ?: "Authentication failed"
                }
                is Response.Loading -> {
                    // Do nothing, already in loading state
                }
            }

            _isLoading.value = false
        }
    }
}