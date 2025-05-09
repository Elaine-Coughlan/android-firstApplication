package com.elaine.minerecipies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class RecipeRecommendationsViewModel(
    private val inventoryViewModel: InventoryViewModel,
    private val recipesViewModel: RecipesViewModel
) : ViewModel() {

    init {
        // Connect inventory updates to recipe recommendations
        viewModelScope.launch {
            inventoryViewModel.inventoryList.collect { inventory ->
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

    // Add a Factory class
    class Factory(
        private val inventoryViewModel: InventoryViewModel,
        private val recipesViewModel: RecipesViewModel
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeRecommendationsViewModel::class.java)) {
                return RecipeRecommendationsViewModel(
                    inventoryViewModel,
                    recipesViewModel
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}