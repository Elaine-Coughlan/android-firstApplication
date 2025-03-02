package com.elaine.minerecipies.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elaine.minerecipies.data.InventoryItem

@Composable
fun InventoryScreen() {

    val items = listOf(
        InventoryItem(name = "Iron Ingot", quantity = 10),
        InventoryItem(name = "Oak Logs", quantity = 5),
        InventoryItem(name = "Crafting Table", quantity = 1)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        InventoryInput()

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(items) { item ->
                InventoryItemRow(item)
            }
        }
    }
}

@Composable
fun InventoryInput() {
    Row {
        TextField(
            value = "Item Name",
            onValueChange = {},
            label = { Text("Item Name") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = "1",
            onValueChange = {},
            label = { Text("Qty") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {}) {
            Text("Add")
        }
    }
}

@Composable
fun InventoryItemRow(item: InventoryItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = "${item.name} (${item.quantity})", modifier = Modifier.weight(1f))

        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInventoryScreen() {
    InventoryScreen()
}