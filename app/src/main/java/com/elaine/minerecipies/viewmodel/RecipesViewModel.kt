package com.elaine.minerecipies.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elaine.minerecipies.data.Blocks.Blocks
import com.elaine.minerecipies.data.database.InventoryItem
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Recipe
import com.elaine.minerecipies.data.RecipeRecommendation
import com.elaine.minerecipies.data.api.loadBlocks
import com.elaine.minerecipies.data.api.loadItems
import com.elaine.minerecipies.data.api.loadRecipes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes


    private val _recommendedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recommendedRecipes: StateFlow<List<Recipe>> = _recommendedRecipes

    private val _filteredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val filteredRecipes: StateFlow<List<Recipe>> = _filteredRecipes

    private val recipeRecommendation = RecipeRecommendation()

    val itemsList: List<Items> = loadItems(context)
    val blocksList: List<Blocks> = loadBlocks(context)

    private val recipeTypeCache = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            _recipes.value = loadRecipes(context)

            _recipes.value.forEach { recipe ->
                recipeTypeCache[recipe.item] =
                    recipeRecommendation.getRecipeType(recipe)

            }

        }

    }

    fun updateRecommendedRecipes(inventory: List<InventoryItem>) {

        viewModelScope.launch {
            val craftableRecipes =
                recipeRecommendation.findCraftableRecipes(_recipes.value, inventory)
            _recommendedRecipes.value = craftableRecipes
            _filteredRecipes.value = craftableRecipes
        }
    }

    fun filterByType(type: String) {
        viewModelScope.launch {
            if (type.isEmpty() || type.equals("All", ignoreCase = true)) {
                _filteredRecipes.value = _recommendedRecipes.value
            } else {
                _filteredRecipes.value = _recommendedRecipes.value.filter { recipe ->
                    val recipeType =
                        recipeTypeCache[recipe.item] ?: recipeRecommendation.getRecipeType(
                            recipe
                        ).also {
                            recipeTypeCache[recipe.item] = it
                        }
                    recipeType.equals(type, ignoreCase = true)
                }
            }
        }
    }

    fun sortByComplexity(ascending: Boolean = true) {
        viewModelScope.launch {
            _filteredRecipes.value = recipeRecommendation.sortRecipesByComplexity(
                _filteredRecipes.value, ascending
            )
        }
    }





    fun sortByResourcesNeeded(ascending: Boolean = true) {
        viewModelScope.launch {
            _filteredRecipes.value = recipeRecommendation.sortRecipesByResourcesNeeded(
                _filteredRecipes.value, ascending
            )
        }
    }





    fun findAlmostCraftableRecipes(inventory: List<InventoryItem>, maxMissingIngredients: Int = 1) {

        viewModelScope.launch {

            _filteredRecipes.value = recipeRecommendation.findAlmostCraftableRecipes(

                _recipes.value, inventory, maxMissingIngredients

            )

        }

    }



    /**

     * Get missing ingredients for a specific recipe

     */

    fun getMissingIngredients(recipe: Recipe, inventory: List<InventoryItem>): List<Pair<String, Int>> {

        return recipeRecommendation.getMissingIngredients(recipe, inventory)

    }





    fun getRecipeType(recipe: Recipe): String {

        return recipeTypeCache[recipe.item] ?: recipeRecommendation.getRecipeType(recipe).also {

            recipeTypeCache[recipe.item] = it

        }

    }







}
