// Update the RecipeScreen.kt file
package com.elaine.minerecipies.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elaine.minerecipies.ui.components.RecipeItem
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import com.elaine.minerecipies.viewmodel.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(context: Context) {
    MineRecipiesTheme {
        val viewModel: RecipesViewModel = hiltViewModel()
        val recipes by viewModel.recipes.collectAsState()
        val items = viewModel.itemsList // Access the items list from the ViewModel
        val blocks = viewModel.blocksList // Access the blocks list from the ViewModel
        var searchQuery by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Add a search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Recipes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Filter recipes based on search query
            val filteredRecipes = if (searchQuery.isEmpty()) {
                recipes
            } else {
                recipes.filter { it.item.contains(searchQuery, ignoreCase = true) }
            }

            // Use a grid layout for better display of recipe cards
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRecipes) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        items = items,
                        blocks = blocks
                    )
                }
            }
        }
    }
}