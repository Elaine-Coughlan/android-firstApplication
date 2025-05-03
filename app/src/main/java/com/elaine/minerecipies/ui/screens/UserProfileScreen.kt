package com.elaine.minerecipies.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elaine.minerecipies.firebase.models.FirebaseUser
import com.elaine.minerecipies.navigation.Login
import com.elaine.minerecipies.navigation.Recipes
import com.elaine.minerecipies.viewmodel.InventoryViewModel
import com.elaine.minerecipies.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    // Use collectAsState(initial) to avoid null states
    val loggedIn = inventoryViewModel.isLoggedIn.collectAsState(initial = false)
    val profile = userProfileViewModel.userProfile.collectAsState(initial = null)
    val loading = userProfileViewModel.isLoading.collectAsState(initial = false)
    val error = userProfileViewModel.errorMessage.collectAsState(initial = null)

    var displayName by remember { mutableStateOf("") }

    // Check login status
    LaunchedEffect(loggedIn.value) {
        if (!loggedIn.value) {
            navController.navigate(Login.route)
        } else {
            userProfileViewModel.loadUserProfile()
        }
    }

    // Update display name when profile is loaded
    LaunchedEffect(profile.value) {
        profile.value?.let {
            displayName = it.displayName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                profile.value?.let { userProfile ->
                    // Email
                    Text(
                        text = "Email: ${userProfile.email}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Display name
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save button
                    Button(
                        onClick = { userProfileViewModel.updateDisplayName(displayName) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign out button
                    OutlinedButton(
                        onClick = {
                            inventoryViewModel.signOut()
                            navController.navigate(Recipes.route) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign Out")
                    }

                    // Delete account button
                    OutlinedButton(
                        onClick = { userProfileViewModel.showDeleteAccountDialog() },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete Account")
                    }
                }
            }

            // Error message
            error.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Account deletion confirmation dialog
            if (userProfileViewModel.showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { userProfileViewModel.hideDeleteAccountDialog() },
                    title = { Text("Delete Account") },
                    text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                userProfileViewModel.deleteAccount()
                                navController.navigate(Recipes.route) {
                                    popUpTo(0)
                                }
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { userProfileViewModel.hideDeleteAccountDialog() }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}