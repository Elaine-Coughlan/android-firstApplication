package com.elaine.minerecipies

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elaine.minerecipies.ui.screens.RecipesScreen
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // To make sure system UI doesn't add padding
        setContent {
            MineRecipiesTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppTopBar() },
                    content = { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text(text = "Minecraft Recipes") },
        navigationIcon = {
            IconButton(onClick = { /* Handle click */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {

    NavHost(
        navController = navController,
        startDestination = "recipes",
        modifier = modifier
    ){
        composable("recipes"){
            RecipesScreen(modifier = modifier, context = LocalContext.current)
        }
    }
}






