package com.elaine.minerecipies.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

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



val allDestinations = listOf(Recipes, Inventory)
