// Update the RecipeItem.kt file
package com.elaine.minerecipies.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elaine.minerecipies.data.Recipe
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Blocks.Blocks
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun RecipeItem(
    recipe: Recipe,
    items: List<Items>,
    blocks: List<Blocks>
) {
    MineRecipiesTheme {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display item image and name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Find the matching item to get its image URL
                    val itemImageUrl = findItemImageUrl(recipe.item, items, blocks)

                    // Display the image using Coil
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = itemImageUrl,
                            contentScale = ContentScale.Fit
                        ),
                        contentDescription = recipe.item,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = recipe.item,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Modified CraftingGrid to show images instead of text
                CraftingGrid(
                    recipeItems = recipe.recipe as List<String?>,
                    items = items,
                    blocks = blocks
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Quantity: ${recipe.quantity}", fontSize = 16.sp)
                Text(text = "Shapeless: ${recipe.shapeless}", fontSize = 16.sp)
            }
        }
    }
}

// Helper function to find the image URL for an item or block
fun findItemImageUrl(name: String, items: List<Items>, blocks: List<Blocks>): String {
    // First try to find among items
    val item = items.find { it.name.equals(name, ignoreCase = true) }
    if (item != null) {
        return item.image
    }

    // Then try to find among blocks
    val block = blocks.find { it.name.equals(name, ignoreCase = true) }
    if (block != null) {
        return block.image
    }

    // Return a default image URL if no match is found
    return "https://minecraft-api.vercel.app/images/items/barrier.png" // Default "unknown" item
}
// Updated CraftingGrid in RecipeItem.kt
@Composable
fun CraftingGrid(
    recipeItems: List<String?>,
    items: List<Items>,
    blocks: List<Blocks>
) {
    MineRecipiesTheme {
        val gridSize = 3  // 3x3 Crafting Grid

        // Create a background color for the crafting table
        val craftingTableBackground = MaterialTheme.colorScheme.surfaceVariant
        val cellBackground = MaterialTheme.colorScheme.surface

        // Create a border for each cell
        val borderColor = MaterialTheme.colorScheme.outline

        Column(
            modifier = Modifier
                .background(craftingTableBackground, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            for (i in 0 until gridSize) {
                Row {
                    for (j in 0 until gridSize) {
                        val index = i * gridSize + j
                        val itemName = if (index < recipeItems.size) recipeItems[index] else ""

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .padding(4.dp)
                                .background(
                                    color = cellBackground,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (itemName?.isNotEmpty() == true) {
                                // Find the image URL for this ingredient
                                val imageUrl = findItemImageUrl(itemName, items, blocks)

                                // Display the ingredient image
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = imageUrl,
                                        contentScale = ContentScale.Fit
                                    ),
                                    contentDescription = itemName,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}