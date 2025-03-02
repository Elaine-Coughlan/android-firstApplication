package com.elaine.minerecipies.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elaine.minerecipies.ui.components.RecipeItem
import com.elaine.minerecipies.viewmodel.RecipesViewModel

@Composable
fun RecipesScreen(modifier: Modifier, context: Context) {
    val viewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory(context))
    val recipes by viewModel.recipes.collectAsState()

    LazyColumn() {
        items(recipes) { recipe ->
            RecipeItem(recipe)
        }
    }
}
