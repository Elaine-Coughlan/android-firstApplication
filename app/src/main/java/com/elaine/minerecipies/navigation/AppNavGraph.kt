package com.elaine.minerecipies.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.ui.screens.InventoryScreen
import com.elaine.minerecipies.ui.screens.LoginScreen
import com.elaine.minerecipies.ui.screens.RecipeRecommendationsScreen
import com.elaine.minerecipies.ui.screens.RecipesScreen
import com.elaine.minerecipies.ui.screens.RegisterScreen
import com.elaine.minerecipies.viewmodel.InventoryViewModel
import com.elaine.minerecipies.viewmodel.RecipeRecommendationsViewModel
import com.elaine.minerecipies.viewmodel.RecipesViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authService: AuthService
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MineRecipies") }
            )
        }
    ) { paddingValues ->
        NavHostProvider(
            modifier = Modifier,
            navController = navController,
            paddingValues = paddingValues,
            authService = authService
        )
    }
}
@Composable
fun NavHostProvider(
    modifier: Modifier,
    navController: NavHostController,
    paddingValues: PaddingValues,
    authService: AuthService
) {
    // Create a state that will be updated when authentication changes
    val authState = remember { mutableStateOf(authService.isUserAuthenticatedInFirebase) }

    // Add an effect that updates the state when auth changes
    LaunchedEffect(Unit) {
        // Set up an observer for auth changes
        authState.value = authService.isUserAuthenticatedInFirebase
    }

    // This effect refreshes the auth state after navigation actions
    LaunchedEffect(navController.currentBackStackEntry) {
        authState.value = authService.isUserAuthenticatedInFirebase
    }

    NavHost(
        navController = navController,
        startDestination = Recipes.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        // Public screens
        composable(route = Recipes.route) {
            val context = LocalContext.current
            RecipesScreen(context)
        }

        // Authentication screens
        composable(route = Login.route) {
            LoginScreen(
                navController = navController,
                onLogin = {
                    // Update auth state immediately after login
                    authState.value = true
                    navController.navigate(Recipes.route) {
                        popUpTo(Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Register.route) {
            RegisterScreen(
                navController = navController,
                onRegister = {
                    // Update auth state immediately after registration
                    authState.value = true
                    navController.navigate(Recipes.route) {
                        popUpTo(Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth-required screens
        composable(route = Inventory.route) {
            // Use the state variable instead of direct call
            if (authState.value) {
                InventoryScreen(
                    navController = navController
                )
            } else {
                // Redirect to login if not logged in
                LaunchedEffect(true) {
                    navController.navigate(Login.route)
                }
            }
        }

        composable(route = RecipeRecommendations.route) {
            // Use the state variable instead of direct call
            if (authState.value) {
                val context = LocalContext.current
                val lifecycleOwner = context as LifecycleOwner

                val inventoryViewModel: InventoryViewModel = hiltViewModel()
                val recipesViewModel: RecipesViewModel = hiltViewModel()
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
            } else {
                // Redirect to login if not logged in
                LaunchedEffect(true) {
                    navController.navigate(Login.route)
                }
            }
        }
    }
}