package com.elaine.minerecipies.data.api

import android.content.Context
import com.elaine.minerecipies.data.Blocks.Blocks
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadRecipes(context: Context): List<Recipe> {
    val json = context.assets.open("recipes.json").bufferedReader().use { it.readText() }

    val type = object : TypeToken<List<Map<String, Any>>>() {}.type
    val rawList: List<Map<String, Any>> = Gson().fromJson(json, type)

    return rawList.mapNotNull { map ->
        try {
            val fullRecipe = (map["recipe"] as? List<*>)?.map { it as? String } ?: emptyList()

            Recipe(
                item = map["item"] as? String ?: "Unknown Item",
                quantity = (map["quantity"] as? Number)?.toInt() ?: 1,
                recipe = ensureGridSize(fullRecipe, 9), // 👈 Ensures 9 slots (3x3)
                shapeless = map["shapeless"] as? Boolean ?: false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null // Ignore bad entries
        }
    }
}

fun ensureGridSize(recipeList: List<String?>, size: Int): List<String> {
    return List(size) { index -> recipeList.getOrNull(index) ?: "" }
}

fun loadItems(context: Context): List<Items> {
    val json = context.assets.open("items.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Items>>() {}.type
    return Gson().fromJson(json, type)
}

fun loadBlocks(context: Context): List<Blocks> {
    val json = context.assets.open("blocks.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Blocks>>() {}.type
    return Gson().fromJson(json, type)
}
