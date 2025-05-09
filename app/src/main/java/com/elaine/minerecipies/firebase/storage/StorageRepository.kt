package com.elaine.minerecipies.firebase.storage

import android.net.Uri
import com.elaine.minerecipies.firebase.auth.Response
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.StorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val authService: AuthService
) : StorageService {

    private val profilePhotosRef get() = storage.reference.child("profile_photos")

    private fun getUserPhotoRef() =
        authService.currentUserId.let { userId ->
            if (userId.isNotEmpty()) {
                profilePhotosRef.child("$userId.jpg")
            } else {
                null
            }
        }

    override suspend fun uploadProfilePhoto(photoUri: Uri): Uri {
        val photoRef = getUserPhotoRef() ?: throw IllegalStateException("User not authenticated")

        // Upload the file
        photoRef.putFile(photoUri).await()

        // Get download URL
        return photoRef.downloadUrl.await()
    }

    override suspend fun getProfilePhotoUrl(): Uri? {
        val photoRef = getUserPhotoRef() ?: return null

        return try {
            photoRef.downloadUrl.await()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteProfilePhoto(): Response<Unit> {
        val photoRef = getUserPhotoRef() ?: return Response.Success(Unit)

        return try {
            photoRef.delete().await()
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(e)
        }
    }
}