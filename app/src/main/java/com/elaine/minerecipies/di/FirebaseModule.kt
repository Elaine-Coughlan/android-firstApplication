package com.elaine.minerecipies.di

import android.content.Context
import com.elaine.minerecipies.firebase.auth.AuthRepository
import com.elaine.minerecipies.firebase.database.FirestoreRepository
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.FirestoreService
import com.elaine.minerecipies.firebase.services.StorageService
import com.elaine.minerecipies.firebase.storage.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        @ApplicationContext context: Context
    ): AuthService =
        AuthRepository(firebaseAuth = auth, context = context)

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirestoreRepository(
        auth: AuthService,
        firestore: FirebaseFirestore
    ): FirestoreService = FirestoreRepository(
        auth = auth,
        firestore = firestore
    )

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideStorageService(
        storage: FirebaseStorage,
        auth: AuthService
    ): StorageService = StorageRepository(storage, auth)
}