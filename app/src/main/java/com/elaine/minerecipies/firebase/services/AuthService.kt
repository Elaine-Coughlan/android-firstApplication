// AuthService.kt
package com.elaine.minerecipies.firebase.services

import android.content.Intent
import android.net.Uri
import com.elaine.minerecipies.firebase.auth.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

typealias FirebaseSignInResponse = Response<FirebaseUser>

interface AuthService {
    val currentUserId: String
    val currentUser: FirebaseUser?
    val isUserAuthenticatedInFirebase: Boolean
    val email: String?
    val displayName: String?
    val authStateFlow: Flow<Boolean>

    //Login
    suspend fun authenticateUser(email: String, password: String): FirebaseSignInResponse
    suspend fun createUser(name: String, email: String, password: String): FirebaseSignInResponse
    suspend fun signOut()
    suspend fun resetPassword(email: String): Response<Unit>

    // Google sign-in
    fun getGoogleSignInIntent(): Intent
    suspend fun handleGoogleSignInResult(data: Intent?): FirebaseSignInResponse

    suspend fun updateProfile(displayName: String): FirebaseSignInResponse
    suspend fun updateProfilePhoto(photoUri: Uri): FirebaseSignInResponse
    suspend fun deleteAccount(): Response<Unit>

}