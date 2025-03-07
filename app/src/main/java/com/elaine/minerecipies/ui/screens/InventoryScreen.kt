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
import com.elaine.minerecipies.ui.components.RadioButtonSingleSelection
import com.elaine.minerecipies.viewmodel.InventoryViewModel



@Composable
fun InventoryScreen() {
    val context = LocalContext.current
    val viewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModel.InventoryViewModelFactory(context)
    )

    val inventoryList by viewModel.inventoryList.collectAsState()
    val selectedType = remember { mutableStateOf("Item") }
    val selectedName = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("1") }
    val manualEntry = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Type:", style = MaterialTheme.typography.titleMedium)
        RadioButtonSingleSelection()

        val options = if (selectedType.value == "Item") {
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
            value = manualEntry.value,
            onValueChange = { manualEntry.value = it },
            label = { Text("Or enter item manually") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = quantity.value,
            onValueChange = { quantity.value = it },
            label = { Text("Quantity") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val itemName = if (manualEntry.value.isNotEmpty()) manualEntry.value else selectedName.value
            val qty = quantity.value.toIntOrNull() ?: 1
            if (itemName.isNotEmpty()) {
                viewModel.addToInventory(name = itemName, quantity = qty, type = selectedType.value)
                manualEntry.value = ""
                selectedName.value = ""
                quantity.value = "1"
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






