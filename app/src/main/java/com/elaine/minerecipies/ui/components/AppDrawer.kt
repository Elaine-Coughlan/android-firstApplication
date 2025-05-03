package com.elaine.minerecipies.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elaine.minerecipies.firebase.services.AuthService
import com.elaine.minerecipies.navigation.Login
import com.elaine.minerecipies.navigation.allDestinations
import com.elaine.minerecipies.navigation.authRequiredDestinations
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavHostController,
    drawerState: DrawerState,
    authService: AuthService,
    content: @Composable () -> Unit
) {
    MineRecipiesTheme {
        val scope = rememberCoroutineScope()
        val isAuthenticated = authService.isUserAuthenticatedInFirebase

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Minecraft Recipes", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        allDestinations.forEach { destination ->
                            NavigationDrawerItem(
                                label = { Text(destination.label) },
                                selected = false,
                                onClick = {
                                    // Check if authentication is required
                                    if (authRequiredDestinations.contains(destination) && !isAuthenticated) {
                                        navController.navigate(Login.route)
                                    } else {
                                        navController.navigate(destination.route)
                                    }
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }

                        // Add login/logout option
                        if (isAuthenticated) {
                            NavigationDrawerItem(
                                label = { Text("Logout") },
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        authService.signOut()
                                        drawerState.close()
                                        navController.navigate(Login.route)
                                    }
                                }
                            )
                        } else {
                            NavigationDrawerItem(
                                label = { Text("Login") },
                                selected = false,
                                onClick = {
                                    navController.navigate(Login.route)
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                }
            }
        ) {
            content()
        }
    }
}