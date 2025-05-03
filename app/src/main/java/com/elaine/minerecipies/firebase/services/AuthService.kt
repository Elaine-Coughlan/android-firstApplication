package com.elaine.minerecipies.firebase.services

import android.net.Uri
import com.elaine.minerecipies.firebase.auth.Response
import com.google.firebase.auth.FirebaseUser

typealias FirebaseSignInResponse = Response<FirebaseUser>

interface AuthService {
    val currentUserId: String
    val currentUser: FirebaseUser?
    val isUserAuthenticatedInFirebase: Boolean
    val email: String?
    val displayName: String?

    suspend fun authenticateUser(email: String, password: String): FirebaseSignInResponse
    suspend fun createUser(name: String, email: String, password: String): FirebaseSignInResponse
    suspend fun signOut()
    suspend fun resetPassword(email: String): Response<Unit>
}