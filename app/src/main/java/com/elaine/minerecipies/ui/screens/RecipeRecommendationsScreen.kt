package com.elaine.minerecipies.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elaine.minerecipies.data.Recipe
import com.elaine.minerecipies.data.database.InventoryItem
import com.elaine.minerecipies.viewmodel.InventoryViewModel
import com.elaine.minerecipies.viewmodel.RecipeRecommendationsViewModel
import com.elaine.minerecipies.viewmodel.RecipesViewModel

@Composable
fun RecipeRecommendationsScreen(
    recommendationsViewmodel: RecipeRecommendationsViewModel,
    recipesViewModel: RecipesViewModel,
    inventoryViewModel: InventoryViewModel
) {
    LaunchedEffect(Unit) {
        recommendationsViewmodel.refreshRecommendations()
    }

    val filteredRecipes by recipesViewModel.filteredRecipes.collectAsState()
    val inventory by inventoryViewModel.inventoryList.collectAsState()

    var selectedType by remember { mutableStateOf("All") }
    var sortOption by remember { mutableStateOf("None") }
    var showMissingIngredients by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recipe Recommendations",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter section
        FilterSection(
            selectedType = selectedType,
            onTypeSelected = {
                selectedType = it
                recommendationsViewmodel.filterRecipesByType(it)
            },
            sortOption = sortOption,
            onSortOptionSelected = {
                sortOption = it
                when (it) {
                    "Complexity (Low to High)" -> recommendationsViewmodel.sortRecipesByComplexity(true)
                    "Complexity (High to Low)" -> recommendationsViewmodel.sortRecipesByComplexity(false)
                    "Resources (Low to High)" -> recommendationsViewmodel.sortRecipesByResourcesNeeded(true)
                    "Resources (High to Low)" -> recommendationsViewmodel.sortRecipesByResourcesNeeded(false)
                    else -> {} // No sorting
                }
            },
            showMissingIngredients = showMissingIngredients,
            onShowMissingIngredientsChanged = { show ->
                showMissingIngredients = show
                if (show) {
                    recommendationsViewmodel.showAlmostCraftableRecipes(1)
                } else {
                    recommendationsViewmodel.filterRecipesByType(selectedType)
                }
            }
        )

        // Recipe list
        if (filteredRecipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showMissingIngredients) {
                        "No recipes found that are almost craftable"
                    } else {
                        "No recipes found with current filters"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredRecipes.size) { index ->
                    val recipe = filteredRecipes[index]
                    RecipeRecommendationCard(
                        recipe = recipe,
                        inventory = inventory,
                        recipesViewModel = recipesViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    sortOption: String,
    onSortOptionSelected: (String) -> Unit,
    showMissingIngredients: Boolean,
    onShowMissingIngredientsChanged: (Boolean) -> Unit
) {
    val types = listOf("All", "Tools", "Weapons", "Food", "Blocks", "Redstone", "Other")
    val sortOptions = listOf(
        "None",
        "Complexity (Low to High)",
        "Complexity (High to Low)",
        "Resources (Low to High)",
        "Resources (High to Low)"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Type filter chips
        Text(
            text = "Filter by Type:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type) }
                )
            }
        }

        // Sort options and almost craftable toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort dropdown
            Text(
                text = "Sort by:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )

            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = sortOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onSortOptionSelected(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Almost craftable switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Show almost craftable recipes")
            Switch(
                checked = showMissingIngredients,
                onCheckedChange = onShowMissingIngredientsChanged
            )
        }
    }
}

@Composable
fun RecipeRecommendationCard(
    recipe: Recipe,
    inventory: List<InventoryItem>,
    recipesViewModel: RecipesViewModel
) {
    val missingIngredients = recipesViewModel.getMissingIngredients(recipe, inventory)
    val isCraftable = missingIngredients.isEmpty()
    val recipeType = recipesViewModel.getRecipeType(recipe)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Recipe header with name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.item,
                    style = MaterialTheme.typography.titleLarge
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCraftable)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = if (isCraftable) "Craftable" else "Missing ingredients",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (isCraftable)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recipe metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Type: $recipeType",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Quantity: ${recipe.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (recipe.shapeless) {
                Text(
                    text = "Shapeless Recipe",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Ingredients section
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Group ingredients by name and count occurrences
            val ingredientCounts = recipe.recipe.groupingBy { it }.eachCount()

            ingredientCounts.forEach { (ingredient, count) ->
                val inventoryItem = inventory.find { it.name == ingredient }
                val available = inventoryItem?.quantity ?: 0
                val isEnough = available >= count

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• $ingredient ($count)",
                        color = if (isEnough) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = "Available: $available",
                        color = if (isEnough) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            // Missing ingredients section
            if (!isCraftable) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Missing ingredients:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(4.dp))

                missingIngredients.forEach { (name, quantity) ->
                    Text(
                        text = "• $name (need $quantity more)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}