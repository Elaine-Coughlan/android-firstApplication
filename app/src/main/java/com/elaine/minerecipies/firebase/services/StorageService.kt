package com.elaine.minerecipies.firebase.services

import android.net.Uri
import com.elaine.minerecipies.firebase.auth.Response

interface StorageService {
    suspend fun uploadProfilePhoto(photoUri: Uri): Uri
    suspend fun getProfilePhotoUrl(): Uri?
    suspend fun deleteProfilePhoto(): Response<Unit>
}