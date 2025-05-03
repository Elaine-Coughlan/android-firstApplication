package com.elaine.minerecipies.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elaine.minerecipies.navigation.Login
import com.elaine.minerecipies.ui.components.AutoCompleteTextField
import com.elaine.minerecipies.ui.components.InventoryItemRow
import com.elaine.minerecipies.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val inventoryList by viewModel.inventoryList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isItemSelected = remember { mutableStateOf(true) }
    val selectedName = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("1") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with login status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Inventory",
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(onClick = { navController.navigate(Login.route) }) {
                Icon(Icons.Default.Person, contentDescription = "Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Item type selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { isItemSelected.value = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Items")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { isItemSelected.value = false },
                modifier = Modifier.weight(1f)
            ) {
                Text("Blocks")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Item selection
        val options = if (isItemSelected.value) {
            viewModel.availableItems.map { it.name }
        } else {
            viewModel.availableBlocks.map { it.name }
        }

        AutoCompleteTextField(
            options = options,
            selectedValue = selectedName.value,
            onValueChange = { selectedName.value = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quantity input
        TextField(
            value = quantity.value,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    quantity.value = it
                }
            },
            label = { Text("Quantity") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add button
        Button(
            onClick = {
                val qty = quantity.value.toIntOrNull() ?: 1
                if (selectedName.value.isNotEmpty() && qty > 0) {
                    viewModel.addItem(
                        name = selectedName.value,
                        quantity = qty,
                        type = if (isItemSelected.value) "Item" else "Block"
                    )
                    selectedName.value = ""
                    quantity.value = "1"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && selectedName.value.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Add to Inventory")
            }
        }

        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LaunchedEffect(it) {
                // Clear error after showing it
                viewModel.clearError()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inventory list
        if (isLoading && inventoryList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (inventoryList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Your inventory is empty")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(inventoryList) { item ->
                    InventoryItemRow(
                        item = item,
                        onDelete = { viewModel.removeItem(item) }
                    )
                }
            }
        }
    }
}