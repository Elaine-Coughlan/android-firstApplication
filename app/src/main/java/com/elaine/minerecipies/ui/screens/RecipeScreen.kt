package com.elaine.minerecipies.ui.screens

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elaine.minerecipies.ui.components.RecipeItem
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import com.elaine.minerecipies.viewmodel.RecipesViewModel

@Composable
fun RecipesScreen(context: Context) {
    MineRecipiesTheme {
        val viewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory(context))
        val recipes by viewModel.recipes.collectAsState()

        LazyColumn() {
            items(recipes) { recipe ->
                RecipeItem(recipe)
            }
        }
    }
}
