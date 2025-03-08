package com.elaine.minerecipies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.elaine.minerecipies.navigation.NavHostProvider
import com.elaine.minerecipies.ui.components.AppDrawer
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            MineRecipiesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = darkColorScheme().onBackground
                ) {
                    MainApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppDrawer(navController, drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Minecraft Recipes") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHostProvider(
                modifier = Modifier,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    MineRecipiesTheme {
        MainApp()
    }
}
