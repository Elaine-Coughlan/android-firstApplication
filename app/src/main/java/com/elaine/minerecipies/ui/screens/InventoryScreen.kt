package com.elaine.minerecipies.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elaine.minerecipies.ui.components.AutoCompleteTextField
import com.elaine.minerecipies.ui.components.InventoryItemRow
import com.elaine.minerecipies.viewmodel.InventoryViewModel


@Composable
fun InventoryScreen() {
    val context = LocalContext.current
    val viewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModel.InventoryViewModelFactory(context)
    )

    val inventoryList by viewModel.inventoryList.collectAsState()
    val isItemSelected = remember { mutableStateOf(true) }
    val selectedName = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("1") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            Button(onClick = { isItemSelected.value = true }) {
                Text("Items")
            }
            Button(onClick = { isItemSelected.value = false }) {
                Text("Blocks")
            }
        }

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

        TextField(
            value = quantity.value,
            onValueChange = { quantity.value = it },
            label = { Text("Quantity") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val qty = quantity.value.toIntOrNull() ?: 1
            if (selectedName.value.isNotEmpty()) {
                // Add to inventory
                viewModel.addToInventory(
                    name = selectedName.value,
                    quantity = qty,
                    type = if (isItemSelected.value) "Item" else "Block"
                )



            }
        }) {
            Text("Add to Inventory")
        }

        LazyColumn {
            items(inventoryList) { item ->
                InventoryItemRow(item, onDelete = { viewModel.removeFromInventory(item) })
            }
        }
    }
}





