package com.elaine.minerecipies.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.elaine.minerecipies.ui.screens.RecipeRecommendationsScreen

interface AppDestination {
    val icon: ImageVector
    val label: String
    val route: String
}

object Recipes : AppDestination {
    override val icon = Icons.Filled.Home
    override val label = "Recipes"
    override val route = "recipes"
}

object Inventory : AppDestination {
    override val icon = Icons.Filled.Add
    override val label = "Inventory"
    override val route = "inventory"
}

object RecipeRecommendations : AppDestination {
    override val icon = Icons.Filled.Star
    override val label = "Recommendations"
    override val route = "recommendations"
}

val allDestinations = listOf(Recipes, Inventory, RecipeRecommendations)
