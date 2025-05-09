package com.elaine.minerecipies.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elaine.minerecipies.navigation.Recipes
import com.elaine.minerecipies.viewmodel.InventoryViewModel
import com.elaine.minerecipies.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Get inventory data and update the profile ViewModel
    val inventory by inventoryViewModel.inventoryList.collectAsState()

    // Update inventory count when inventory changes
    LaunchedEffect(inventory) {
        viewModel.updateInventoryCount(inventory.size)
    }

    // Initialize profile image state with current user's photo URL
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        FirebaseAuth.getInstance().currentUser?.photoUrl?.let {
            photoUri = it
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
            viewModel.updateProfilePhoto(it)
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
                },
                actions = {
                    IconButton(onClick = { viewModel.showDeleteAccountDialog() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Account")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Create/edit recipes action
                    navController.navigate("editRecipe/new")
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Recipe",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Profile content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile photo
                    Box(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val context = LocalContext.current
                        if (photoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(photoUri)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Camera icon overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // User info card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Personal Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            profile?.let { user ->
                                // Display name
                                ProfileInfoRow(
                                    icon = Icons.Default.Person,
                                    label = "Name",
                                    value = user.displayName,
                                    editable = true,
                                    onEdit = { viewModel.showEditDisplayNameDialog() }
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                // Email
                                ProfileInfoRow(
                                    icon = Icons.Default.Email,
                                    label = "Email",
                                    value = user.email,
                                    editable = false
                                )

                            }
                        }
                    }

                    // Stats card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "App Activity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    icon = Icons.Default.Inventory,
                                    value = viewModel.inventoryCount.toString(),
                                    label = "Inventory Items"
                                )

                                StatItem(
                                    icon = Icons.Default.MenuBook,
                                    value = viewModel.recipesCount.toString(),
                                    label = "Recipes"
                                )

                                StatItem(
                                    icon = Icons.Default.CheckCircle,
                                    value = viewModel.craftableCount.toString(),
                                    label = "Craftable"
                                )
                            }
                        }
                    }

                    // Account actions
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            ActionButton(
                                icon = Icons.Default.Password,
                                text = "Change Password",
                                onClick = { viewModel.showResetPasswordDialog() }
                            )

                            ActionButton(
                                icon = Icons.Default.Logout,
                                text = "Sign Out",
                                onClick = {
                                    viewModel.signOut()
                                    navController.navigate(Recipes.route) {
                                        popUpTo(0)
                                    }
                                },
                                color = MaterialTheme.colorScheme.primary
                            )

                            ActionButton(
                                icon = Icons.Default.DeleteForever,
                                text = "Delete Account",
                                onClick = { viewModel.showDeleteAccountDialog() },
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Error message
            errorMessage?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(it)
                }
            }

            // Edit display name dialog
            if (viewModel.showEditNameDialog) {
                var newName by remember { mutableStateOf(profile?.displayName ?: "") }

                AlertDialog(
                    onDismissRequest = { viewModel.hideEditDisplayNameDialog() },
                    title = { Text("Edit Display Name") },
                    text = {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateDisplayName(newName)
                                viewModel.hideEditDisplayNameDialog()
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.hideEditDisplayNameDialog() }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Reset password dialog
            if (viewModel.showResetPwdDialog) {
                var email by remember { mutableStateOf(profile?.email ?: "") }

                AlertDialog(
                    onDismissRequest = { viewModel.hideResetPasswordDialog() },
                    title = { Text("Reset Password") },
                    text = {
                        Column {
                            Text("We'll send a password reset link to your email address.")
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.resetPassword(email)
                                viewModel.hideResetPasswordDialog()
                            }
                        ) {
                            Text("Send Link")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.hideResetPasswordDialog() }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Delete account confirmation dialog
            if (viewModel.showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.hideDeleteAccountDialog() },
                    title = { Text("Delete Account") },
                    text = {
                        Text("Are you sure you want to delete your account? This action cannot be undone and all your data will be lost.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteAccount()
                                navController.navigate(Recipes.route) {
                                    popUpTo(0)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.hideDeleteAccountDialog() }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    editable: Boolean = false,
    onEdit: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (editable) {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}