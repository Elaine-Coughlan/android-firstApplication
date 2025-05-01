package com.elaine.minerecipies.navigation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elaine.minerecipies.ui.screens.InventoryScreen
import com.elaine.minerecipies.ui.screens.RecipeRecommendationsScreen
import com.elaine.minerecipies.ui.screens.RecipesScreen
import com.elaine.minerecipies.viewmodel.InventoryViewModel
import com.elaine.minerecipies.viewmodel.RecipeRecommendationsViewModel
import com.elaine.minerecipies.viewmodel.RecipesViewModel

@Composable
fun NavHostProvider(
    modifier: Modifier,
    navController: NavHostController,
    paddingValues: PaddingValues
) {

    val context = LocalContext.current

    val inventoryViewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModel.InventoryViewModelFactory(context)
    )

    val recipesViewModel: RecipesViewModel = viewModel(
        factory = RecipesViewModel.Factory(context)
    )

    NavHost(
        navController = navController,
        startDestination = Recipes.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(route = Recipes.route) { RecipesScreen(LocalContext.current) }
        composable(route = Inventory.route) { InventoryScreen() }
        composable(route = RecipeRecommendations.route) {
            val lifecycleOwner = LocalContext.current as LifecycleOwner
            val recommendationsViewModel = RecipeRecommendationsViewModel(
                inventoryViewModel = inventoryViewModel,
                recipesViewModel = recipesViewModel,
                lifecycleOwner = lifecycleOwner
            )

            RecipeRecommendationsScreen(
                recommendationsViewmodel = recommendationsViewModel,
                recipesViewModel = recipesViewModel,
                inventoryViewModel = inventoryViewModel,
            )
        }

    }


}

