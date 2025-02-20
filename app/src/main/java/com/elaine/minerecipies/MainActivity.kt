package com.elaine.minerecipies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Recipe
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import com.elaine.minerecipies.util.loadItems
import com.elaine.minerecipies.util.loadRecipes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()//Top bar disappears (Clock, wifi, battery)
        setContent {
            MineRecipiesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Recipes(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Recipes(modifier: Modifier = Modifier){
    val context = LocalContext.current
    val recipes = remember { loadRecipes(context) }
    val items = remember { loadItems(context) }

    LazyColumn (modifier = modifier){
        items(recipes){recipe ->
            val item = items.find{
                it.name == recipe.item
            }
            RecipeItem(recipe, item)
        }
    }
}



@Composable
fun RecipeItem(recipe: Recipe, item: Items?) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        //elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            item?.let {
                /**Image(
                    painter = rememberImagePainter(it.image),
                    contentDescription = it.name,
                    modifier = Modifier.size(64.dp)
                )*/
                Text(text = it.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = it.description, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Quantity: ${recipe.quantity}")
            Text(text = "Shapeless: ${recipe.shapeless}")
        }
    }
}
