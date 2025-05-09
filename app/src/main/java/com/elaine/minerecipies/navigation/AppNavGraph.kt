
package com.elaine.minerecipies.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elaine.minerecipies.ui.screens.OnboardingScreen
import com.elaine.minerecipies.ui.screens.UserProfileScreen
import com.elaine.minerecipies.utils.OnboardingPreferences
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


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
    val context = LocalContext.current
    val onboardingPreferences = remember { OnboardingPreferences(context) }
    val hasCompletedOnboardingState = onboardingPreferences.hasCompletedOnboarding.collectAsState(initial = false)
    val hasCompletedOnboarding = hasCompletedOnboardingState.value

    // Collect the auth state from authService.authStateFlow
    val isAuthenticatedState = authService.authStateFlow.collectAsState(initial = authService.isUserAuthenticatedInFirebase)
    val isAuthenticated = isAuthenticatedState.value

    // Determine the start destination based on onboarding status
    val startDestination = if (hasCompletedOnboarding) Recipes.route else Onboarding.route

    // Debug logging to help verify auth state is working
    LaunchedEffect(isAuthenticated) {
        println("Auth state changed: $isAuthenticated")
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,  // Use the determined start destination
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(route = Onboarding.route) {
            OnboardingScreen(
                navController = navController,
                onOnboardingComplete = {
                    // Mark onboarding as completed and navigate to Recipes
                    MainScope().launch {
                        onboardingPreferences.completeOnboarding()
                    }
                    navController.navigate(Recipes.route) {
                        popUpTo(Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Public screens
        composable(route = Recipes.route) {
            val context = LocalContext.current
            RecipesScreen(context)
        }

        // Authentication screens
        composable(route = Login.route) {
            // Only show login screen if not authenticated
            if (!isAuthenticated) {
                LoginScreen(
                    navController = navController,
                    onLogin = {
                        // Navigate to Recipes screen after successful login
                        navController.navigate(Recipes.route) {
                            popUpTo(Login.route) { inclusive = true }
                        }
                    }
                )
            } else {
                // If already authenticated, redirect to Recipes
                LaunchedEffect(Unit) {
                    navController.navigate(Recipes.route) {
                        popUpTo(Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(route = "profile") {
            // Ensure user is authenticated for profile screen
            if (isAuthenticated) {
                UserProfileScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Login.route)
                }
            }
        }

        composable(route = Register.route) {
            // Only show register screen if not authenticated
            if (!isAuthenticated) {
                RegisterScreen(
                    navController = navController,
                    onRegister = {
                        navController.navigate(Recipes.route) {
                            popUpTo(Register.route) { inclusive = true }
                        }
                    }
                )
            } else {
                // If already authenticated, redirect to Recipes
                LaunchedEffect(Unit) {
                    navController.navigate(Recipes.route) {
                        popUpTo(Register.route) { inclusive = true }
                    }
                }
            }
        }

        // Auth-required screens
        composable(route = Inventory.route) {
            if (isAuthenticated) {
                InventoryScreen(
                    navController = navController
                )
            } else {
                // Redirect to login if not logged in
                LaunchedEffect(Unit) {
                    navController.navigate(Login.route)
                }
            }
        }

        composable(route = RecipeRecommendations.route) {
            if (isAuthenticated) {
                val inventoryViewModel: InventoryViewModel = hiltViewModel()
                val recipesViewModel: RecipesViewModel = hiltViewModel()

                // Create RecipeRecommendationsViewModel using Factory
                val factory = RecipeRecommendationsViewModel.Factory(
                    inventoryViewModel = inventoryViewModel,
                    recipesViewModel = recipesViewModel
                )

                val recommendationsViewModel: RecipeRecommendationsViewModel = viewModel(factory = factory)
                RecipeRecommendationsScreen(
                    recommendationsViewmodel = recommendationsViewModel,
                    recipesViewModel = recipesViewModel,
                    inventoryViewModel = inventoryViewModel
                )
            } else {
                // Redirect to login if not logged in
                LaunchedEffect(Unit) {
                    navController.navigate(Login.route)
                }
            }
        }
    }

    //listener to handle auth state changes
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            // Check if the current destination requires authentication
            val destinationRoute = destination.route
            if (destinationRoute != null && authRequiredDestinations.any { it.route == destinationRoute } && !isAuthenticated) {
                navController.navigate(Login.route)
            }
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
