package com.example.dam_tp_1.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dam_tp_1.ui.theme.Primary

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    activeFiltersCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Rechercher un produit...",
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Primary
                )
            },
            trailingIcon = if (query.isNotBlank()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Effacer",
                            tint = Color.Gray
                        )
                    }
                }
            } else null,
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = Primary,
                focusedContainerColor = Primary.copy(alpha = 0.03f),
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.03f)
            )
        )

        // Filter Button with Badge
        BadgedBox(
            badge = {
                if (activeFiltersCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    ) {
                        Text(
                            activeFiltersCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        ) {
            FilledTonalIconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Primary.copy(alpha = 0.1f),
                    contentColor = Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtres",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
