package com.elaine.minerecipies.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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


@Composable
fun RecipeItem(recipe: Recipe) {
    MineRecipiesTheme {

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = recipe.item,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                CraftingGrid(recipe.recipe as List<String?>)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Quantity: ${recipe.quantity}", fontSize = 16.sp)
                Text(text = "Shapeless: ${recipe.shapeless}", fontSize = 16.sp)

            }
        }
    }
}
//Crafting bench
@Composable
fun CraftingGrid(recipeItems: List<String?>){
   MineRecipiesTheme {
       val gridSize = 3  // 3x3 Crafting Grid

       Column {
           for (i in 0 until gridSize) {
               Row {
                   for (j in 0 until gridSize) {
                       val index = i * gridSize + j
                       val itemName = if (index < recipeItems.size) recipeItems[index] else ""

                       Box(
                           modifier = Modifier
                               .size(48.dp)
                               .background(MaterialTheme.colorScheme.surface),
                           contentAlignment = Alignment.Center
                       ) {
                           if (itemName != null) {
                               Text(text = itemName, fontSize = 12.sp)
                           }
                       }

                       Spacer(modifier = Modifier.width(4.dp))
                   }
               }
               Spacer(modifier = Modifier.height(4.dp))
           }
       }
   }
}
