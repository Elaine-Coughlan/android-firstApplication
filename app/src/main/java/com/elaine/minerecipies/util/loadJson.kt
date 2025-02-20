package com.elaine.minerecipies.util

import android.content.Context
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadRecipes(context: Context): List<Recipe> {
    val json = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Recipe>>() {}.type
    return Gson().fromJson(json, type)
}

fun loadItems(context: Context): List<Items> {
    val json = context.assets.open("items.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Items>>() {}.type
    return Gson().fromJson(json, type)
}
