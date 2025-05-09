package com.elaine.minerecipies.firebase.database

import com.elaine.minerecipies.data.database.InventoryItem
import com.elaine.minerecipies.firebase.models.FirebaseInventoryItem
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.firebase.services.FirestoreService
import com.elaine.minerecipies.firebase.services.InventoryItems
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val auth: AuthService,
    private val firestore: FirebaseFirestore
) : FirestoreService {

    private val INVENTORY_COLLECTION = "inventory"
    private val USER_EMAIL = "email"

    override suspend fun getAll(email: String): InventoryItems {
        return firestore.collection(INVENTORY_COLLECTION)
            .whereEqualTo(USER_EMAIL, email)
            .dataObjects<FirebaseInventoryItem>()
            .map { items ->
                items.map { firebaseItem ->
                    InventoryItem(
                        id = firebaseItem.id.toIntOrNull() ?: 0,
                        name = firebaseItem.name,
                        quantity = firebaseItem.quantity,
                        type = firebaseItem.type
                    )
                }
            }
    }

    override suspend fun get(email: String, itemId: String): InventoryItem? {
        val document = firestore.collection(INVENTORY_COLLECTION)
            .document(itemId)
            .get()
            .await()

        val firebaseItem = document.toObject(FirebaseInventoryItem::class.java)

        return if (firebaseItem != null && firebaseItem.email == email) {
            InventoryItem(
                id = firebaseItem.id.toIntOrNull() ?: 0,
                name = firebaseItem.name,
                quantity = firebaseItem.quantity,
                type = firebaseItem.type
            )
        } else {
            null
        }
    }

    override suspend fun insert(email: String, item: InventoryItem) {
        val firebaseItem = FirebaseInventoryItem(
            name = item.name,
            quantity = item.quantity,
            type = item.type,
            email = email,
            updatedAt = Timestamp.now()
        )

        firestore.collection(INVENTORY_COLLECTION)
            .add(firebaseItem)
            .await()
    }

    override suspend fun update(email: String, item: InventoryItem) {
        val firebaseItem = FirebaseInventoryItem(
            id = item.id.toString(),
            name = item.name,
            quantity = item.quantity,
            type = item.type,
            email = email,
            updatedAt = Timestamp.now()
        )

        firestore.collection(INVENTORY_COLLECTION)
            .document(item.id.toString())
            .set(firebaseItem)
            .await()
    }

    override suspend fun delete(email: String, itemId: String) {
        firestore.collection(INVENTORY_COLLECTION)
            .document(itemId)
            .delete()
            .await()
    }

    // Add to your FirestoreRepository class

    override suspend fun countUserRecipes(email: String): Int {
        return try {
            val snapshot = firestore.collection("recipes")
                .whereEqualTo("createdBy", email)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun countCraftableRecipes(email: String): Int {
        // This would require more implementation based on your specific app logic
        // For example, comparing user's inventory with available recipes
        return 0 // Placeholder
    }

    override suspend fun deleteUserData(email: String) {
        // Get all documents for this user
        val snapshot = firestore.collection(INVENTORY_COLLECTION)
            .whereEqualTo(USER_EMAIL, email)
            .get()
            .await()

        // Delete each document
        for (document in snapshot.documents) {
            document.reference.delete().await()
        }

        // You might want to also delete recipes, settings, etc. created by this user
    }
}