package com.elaine.minerecipies.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeRecommendationsViewModel(
    private val inventoryViewModel: InventoryViewModel,
    private val recipesViewModel: RecipesViewModel,
    lifecycleOwner: LifecycleOwner
) {

    init {
        // Connect inventory updates to recipe recommendations
        lifecycleOwner.lifecycleScope.launch {
            inventoryViewModel.inventoryList.collectLatest { inventory ->
                recipesViewModel.updateRecommendedRecipes(inventory)
            }
        }
    }

    fun filterRecipesByType(type: String) {
        recipesViewModel.filterByType(type)
    }

    fun sortRecipesByComplexity(ascending: Boolean = true) {
        recipesViewModel.sortByComplexity(ascending)
    }

    fun sortRecipesByResourcesNeeded(ascending: Boolean = true) {
        recipesViewModel.sortByResourcesNeeded(ascending)
    }

    fun showAlmostCraftableRecipes(maxMissingIngredients: Int = 1) {
        recipesViewModel.findAlmostCraftableRecipes(
            inventoryViewModel.inventoryList.value,
            maxMissingIngredients
        )
    }

    fun refreshRecommendations() {
        recipesViewModel.updateRecommendedRecipes(inventoryViewModel.inventoryList.value)
    }




}