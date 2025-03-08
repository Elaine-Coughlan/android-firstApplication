package com.elaine.minerecipies.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.elaine.minerecipies.ui.theme.MineRecipiesTheme


@Composable
fun AutoCompleteTextField(
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    MineRecipiesTheme {

        var expanded by remember { mutableStateOf(false) }
        var searchText by remember { mutableStateOf(selectedValue) }

        Box {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    expanded = it.isNotEmpty()
                    onValueChange(it)
                },
                label = { Text("Search") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.filter { it.contains(searchText, ignoreCase = true) }
                    .take(5)
                    .forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                searchText = option
                                onValueChange(option)
                                expanded = false
                            }
                        )
                    }
            }
        }
    }
}