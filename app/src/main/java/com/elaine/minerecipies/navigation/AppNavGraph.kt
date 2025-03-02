package com.elaine.minerecipies.navigation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elaine.minerecipies.ui.screens.InventoryScreen
import com.elaine.minerecipies.ui.screens.RecipesScreen

@Composable
fun NavHostProvider(
    modifier: Modifier,
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Recipes.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(route = Recipes.route) { RecipesScreen(modifier, LocalContext.current) }
        composable(route = Inventory.route) { InventoryScreen() }
    }
}

