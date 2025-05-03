package com.elaine.minerecipies.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


data class FirebaseUser(
    @DocumentId val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val lastLogin: Timestamp = Timestamp.now()
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "")
}