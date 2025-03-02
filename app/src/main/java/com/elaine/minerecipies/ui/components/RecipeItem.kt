package com.elaine.minerecipies.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Recipe


@Composable
fun RecipeItem(recipe: Recipe) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = recipe.item,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            //TODO crafting grid display

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Quantity: ${recipe.quantity}", fontSize = 16.sp)
            Text(text = "Shapeless: ${recipe.shapeless}", fontSize = 16.sp)

        }
    }
}

@Composable
fun CraftingGrid(recipeItems: List<String>){

}
