package com.example.dam_tp_1.components

import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dam_tp_1.data.FilterState
import com.example.dam_tp_1.data.SortOption
import com.example.dam_tp_1.model.ProductType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    filterState: FilterState,
    availableCountries: List<String>,
    onTypeToggle: (ProductType) -> Unit,
    onCountryToggle: (String) -> Unit,
    onFavoritesToggle: () -> Unit,
    onSortChange: (SortOption) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Column(modifier = modifier) {
            // Row 1: Types de produits
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.width(4.dp))

                ProductType.entries.forEach { type ->
                    FilterChip(
                        selected = type in filterState.selectedTypes,
                        onClick = { onTypeToggle(type) },
                        label = { Text(type.displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = type.accentColor.copy(alpha = 0.2f),
                            selectedLabelColor = type.accentColor
                        )
                    )
                }

                Spacer(Modifier.width(4.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Row 2: Pays et options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.width(4.dp))

                // Chip Favoris
                FilterChip(
                    selected = filterState.showFavoritesOnly,
                    onClick = onFavoritesToggle,
                    label = { Text("Favoris") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (filterState.showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.error
                    )
                )

                // Pays disponibles
                availableCountries.take(3).forEach { country ->
                    FilterChip(
                        selected = country in filterState.selectedCountries,
                        onClick = { onCountryToggle(country) },
                        label = { Text(country) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }

                // Menu de tri
                Box {
                    FilterChip(
                        selected = false,
                        onClick = { showSortMenu = true },
                        label = { Text("Tri: ${filterState.sortBy.displayName}") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.displayName) },
                                onClick = {
                                    onSortChange(option)
                                    showSortMenu = false
                                },
                                leadingIcon = {
                                    if (option == filterState.sortBy) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Bouton Clear All (si des filtres sont actifs)
                if (filterState.selectedTypes.isNotEmpty() ||
                    filterState.selectedCountries.isNotEmpty() ||
                    filterState.showFavoritesOnly ||
                    filterState.searchQuery.isNotBlank()) {

                    FilterChip(
                        selected = false,
                        onClick = onClearFilters,
                        label = { Text("Effacer") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            labelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }

                Spacer(Modifier.width(4.dp))
            }
        }
    }
}
