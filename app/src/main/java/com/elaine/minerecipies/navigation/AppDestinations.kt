package com.elaine.minerecipies.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

interface AppDestination {
    val icon: ImageVector
    val label: String
    val route: String
}

object Login : AppDestination {
    override val icon = Icons.Filled.Person
    override val label = "Login"
    override val route = "login"
}

object Register : AppDestination {
    override val icon = Icons.Filled.Person
    override val label = "Register"
    override val route = "register"
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
val authRequiredDestinations = listOf(Inventory, RecipeRecommendations)
val userSignedOutDestinations = listOf(Login, Register)