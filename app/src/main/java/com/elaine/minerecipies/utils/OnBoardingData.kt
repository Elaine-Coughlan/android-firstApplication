package com.elaine.minerecipies.utils

import androidx.compose.ui.graphics.Color
import com.elaine.minerecipies.R
import com.elaine.minerecipies.data.OnboardingPage

object OnboardingData {
    // Define app-specific color palette for onboarding
    private val inventoryColor = Color(0xFF4CAF50)  // Green for inventory
    private val recipesColor = Color(0xFF2196F3)    // Blue for recipes
    private val craftingColor = Color(0xFFFFA000)   // Amber for crafting
    private val recommendationsColor = Color(0xFFE91E63) // Pink for recommendations

    val pages = listOf(
        OnboardingPage(
            title = "Welcome to MineRecipies",
            description = "Your ultimate companion for Minecraft crafting! Browse recipes, manage your inventory, and craft more efficiently.",
            imageRes = R.drawable.ic_welcome,
            backgroundColor = Color(0xFF3F51B5)  // Primary app color
        ),
        OnboardingPage(
            title = "Browse Recipes",
            description = "Explore hundreds of Minecraft recipes with detailed crafting requirements and instructions.",
            imageRes = R.drawable.ic_recipe,
            backgroundColor = recipesColor
        ),
        OnboardingPage(
            title = "Manage Your Inventory",
            description = "Keep track of your materials by adding them to your inventory. Swipe items to remove them when used.",
            imageRes = R.drawable.ic_inventory,
            backgroundColor = inventoryColor
        ),
        OnboardingPage(
            title = "Recipe Recommendations",
            description = "Get personalized recipe suggestions based on your current inventory. Never waste materials again!",
            imageRes = R.drawable.ic_recommendations,
            backgroundColor = recommendationsColor
        ),
        OnboardingPage(
            title = "Ready to Craft!",
            description = "Sign up to sync your inventory across devices or continue as a guest to start crafting right away.",
            imageRes = R.drawable.ic_google,
            backgroundColor = craftingColor
        )
    )
}