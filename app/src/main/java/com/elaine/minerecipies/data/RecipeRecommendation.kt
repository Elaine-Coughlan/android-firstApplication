package com.elaine.minerecipies.data

class RecipeRecommendation {


    fun findCraftableRecipes(recipes: List<Recipe>, inventory: List<InventoryItem>): List<Recipe> {
        return recipes.filter { recipe ->
            canCraftRecipe(recipe, inventory)
        }
    }


    private fun canCraftRecipe(recipe: Recipe, inventory: List<InventoryItem>): Boolean {
        // Count how many of each ingredient is needed
        val ingredientCounts = recipe.recipe.groupingBy { it }.eachCount()

        // Check if we have enough of each ingredient in inventory
        return ingredientCounts.all { (ingredient, count) ->
            val inventoryItem = inventory.find { it.name == ingredient }
            inventoryItem != null && inventoryItem.quantity >= count
        }
    }


    fun filterRecipesByType(recipes: List<Recipe>, type: String): List<Recipe> {
        if (type.equals("All", ignoreCase = true)) {
            return recipes
        }

        // Get recipe types based on naming conventions
        return recipes.filter { recipe ->
            getRecipeType(recipe).equals(type, ignoreCase = true)
        }
    }


    fun sortRecipesByComplexity(recipes: List<Recipe>, ascending: Boolean = true): List<Recipe> {
        return if (ascending) {
            recipes.sortedBy { it.recipe.size }
        } else {
            recipes.sortedByDescending { it.recipe.size }
        }
    }


    fun sortRecipesByResourcesNeeded(recipes: List<Recipe>, ascending: Boolean = true): List<Recipe> {
        return if (ascending) {
            // Sort by unique ingredients count
            recipes.sortedBy { it.recipe.distinct().size }
        } else {
            recipes.sortedByDescending { it.recipe.distinct().size }
        }
    }


    fun findAlmostCraftableRecipes(
        recipes: List<Recipe>,
        inventory: List<InventoryItem>,
        maxMissingIngredients: Int = 1
    ): List<Recipe> {
        return recipes.filter { recipe ->
            val missingIngredients = getMissingIngredients(recipe, inventory)
            missingIngredients.size <= maxMissingIngredients && missingIngredients.isNotEmpty()
        }
    }


    fun getMissingIngredients(recipe: Recipe, inventory: List<InventoryItem>): List<Pair<String, Int>> {
        val missingIngredients = mutableListOf<Pair<String, Int>>()

        // Count how many of each ingredient is needed
        val ingredientCounts = recipe.recipe.groupingBy { it }.eachCount()

        // Check each ingredient against inventory
        ingredientCounts.forEach { (ingredient, requiredCount) ->
            val inventoryItem = inventory.find { it.name == ingredient }
            val availableCount = inventoryItem?.quantity ?: 0

            if (availableCount < requiredCount) {
                missingIngredients.add(Pair(ingredient, requiredCount - availableCount))
            }
        }

        return missingIngredients
    }


    fun getRecipeType(recipe: Recipe): String {
        val item = recipe.item.lowercase()

        return when {
            item.contains("axe") || item.contains("pickaxe") || item.contains("shovel") ||
                    item.contains("hoe") || item.contains("shears") -> "Tools"
            item.contains("sword") || item.contains("bow") || item.contains("arrow") ||
                    item.contains("shield") || item.contains("trident") -> "Weapons"
            item.contains("apple") || item.contains("bread") || item.contains("cake") ||
                    item.contains("cookie") || item.contains("steak") ||
                    item.contains("porkchop") || item.contains("fish") -> "Food"
            item.contains("block") || item.contains("planks") || item.contains("stone") ||
                    item.contains("brick") || item.contains("glass") -> "Blocks"
            item.contains("redstone") || item.contains("repeater") || item.contains("comparator") ||
                    item.contains("piston") -> "Redstone"
            else -> "Other"
        }
    }
}

