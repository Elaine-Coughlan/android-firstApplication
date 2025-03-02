package com.elaine.minerecipies.data

import android.content.Context
import com.elaine.minerecipies.util.loadRecipes

class RecipesRepository(private val context: Context) {

    fun getRecipes(): List<Recipe>{
        return loadRecipes(context)
    }
}