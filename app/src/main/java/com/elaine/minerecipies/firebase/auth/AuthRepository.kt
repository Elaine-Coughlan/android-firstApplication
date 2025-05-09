package com.elaine.minerecipies.firebase.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.elaine.minerecipies.R
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.FirebaseSignInResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
) : AuthService {

    private val _authStateFlow = MutableStateFlow(firebaseAuth.currentUser != null)
    override val authStateFlow: Flow<Boolean> = _authStateFlow.asStateFlow()

    init {
        // Listen for Firebase Auth changes
        firebaseAuth.addAuthStateListener { auth ->
            _authStateFlow.value = auth.currentUser != null
        }
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    override val currentUserId: String
        get() = firebaseAuth.currentUser?.uid.orEmpty()

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override val isUserAuthenticatedInFirebase: Boolean
        get() = firebaseAuth.currentUser != null

    override val email: String?
        get() = firebaseAuth.currentUser?.email

    override val displayName: String?
        get() = firebaseAuth.currentUser?.displayName

    override suspend fun authenticateUser(email: String, password: String): FirebaseSignInResponse {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Response.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }

    override suspend fun createUser(name: String, email: String, password: String): FirebaseSignInResponse {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()

            Response.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }

    override fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override suspend fun handleGoogleSignInResult(data: Intent?): FirebaseSignInResponse {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken!!

            // Authenticate with Firebase using the Google ID token
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            Response.Success(authResult.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }

    override suspend fun signOut() {
        // Sign out from Firebase
        firebaseAuth.signOut()
        // Sign out from Google
        googleSignInClient.signOut().await()
    }

    override suspend fun resetPassword(email: String): Response<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }


    // Add to your existing AuthRepository class
    override suspend fun updateProfile(displayName: String): FirebaseSignInResponse {
        return try {
            val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(profileUpdates).await()
            Response.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }

    override suspend fun updateProfilePhoto(photoUri: Uri): FirebaseSignInResponse {
        return try {
            val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build()

            user.updateProfile(profileUpdates).await()
            Response.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }

    override suspend fun deleteAccount(): Response<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
            user.delete().await()
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }

}