package com.elaine.minerecipies.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FirebaseInventoryItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val type: String = "",
    val email: String = "",
    val updatedAt: Timestamp = Timestamp.now()
) {
    // Empty constructor for Firestore
    constructor() : this("", "", 0, "", "")
}
