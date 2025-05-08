package com.elaine.minerecipies.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elaine.minerecipies.data.Items
import com.elaine.minerecipies.data.Blocks.Blocks
import com.elaine.minerecipies.data.database.InventoryItem
import com.elaine.minerecipies.ui.components.findItemImageUrl
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableInventoryItemRow(
    item: InventoryItem,
    items: List<Items>,
    blocks: List<Blocks>,
    onDelete: () -> Unit
) {
    MineRecipiesTheme {
        val dismissState = rememberDismissState(
            confirmStateChange = {
                if (it == DismissValue.DismissedToStart) {
                    onDelete()
                    true
                } else {
                    false
                }
            }
        )

        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            dismissThresholds = { FractionalThreshold(0.5f) },
            background = {
                val color = Color.Red.copy(alpha = 0.8f)
                val alignment = Alignment.CenterEnd

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 16.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            },
            dismissContent = {
                InventoryItemCard(item, items, blocks)
            }
        )

        // Add a small spacer between items
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    items: List<Items>,
    blocks: List<Blocks>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Find image URL for the item or block
            val imageUrl = findItemImageUrl(item.name, items, blocks)

            // Item image
            androidx.compose.foundation.Image(
                painter = coil.compose.rememberAsyncImagePainter(
                    model = imageUrl
                ),
                contentDescription = item.name,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Item details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Quantity: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}