package com.elaine.minerecipies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.navigation.NavHostProvider
import com.elaine.minerecipies.ui.components.AppDrawer
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authService: AuthService

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            MineRecipiesTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                AppDrawer(
                    navController = navController,
                    drawerState = drawerState,
                    authService = authService
                ) {
                    // Your main scaffold content here
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("MineRecipies") },
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
                            paddingValues = paddingValues,
                            authService = authService
                        )
                    }
                }
            }
        }
    }
}