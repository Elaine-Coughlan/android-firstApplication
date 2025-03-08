package com.elaine.minerecipies.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elaine.minerecipies.data.database.InventoryItem
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme

@Composable
fun InventoryItemRow(item: InventoryItem, onDelete: () -> Unit) {
    MineRecipiesTheme {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = "${item.name} (${item.quantity})", modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}