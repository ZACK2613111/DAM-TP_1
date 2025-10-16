package com.example.dam_tp_1.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_tp_1.data.FilterState
import com.example.dam_tp_1.data.SortOption
import com.example.dam_tp_1.model.ProductType
import com.example.dam_tp_1.ui.theme.*

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
    // Map pays vers emoji drapeaux
    val countryFlags = mapOf(
        "France" to "🇫🇷",
        "Algérie" to "🇩🇿",
        "Maroc" to "🇲🇦",
        "Tunisie" to "🇹🇳",
        "Canada" to "🇨🇦",
        "États-Unis" to "🇺🇸",
        "Royaume-Uni" to "🇬🇧",
        "Allemagne" to "🇩🇪",
        "Italie" to "🇮🇹",
        "Espagne" to "🇪🇸",
        "Belgique" to "🇧🇪",
        "Suisse" to "🇨🇭",
        "Portugal" to "🇵🇹",
        "Japon" to "🇯🇵",
        "Chine" to "🇨🇳"
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // === ROW 1: Types de produits ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductType.entries.forEach { type ->
                AssistChip(
                    onClick = { onTypeToggle(type) },
                    label = {
                        Text(
                            type.displayName,
                            fontWeight = if (type in filterState.selectedTypes)
                                FontWeight.Bold
                            else
                                FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (type in filterState.selectedTypes)
                            type.accentColor.copy(alpha = 0.15f)
                        else
                            Color.White,
                        labelColor = if (type in filterState.selectedTypes)
                            type.accentColor
                        else
                            Color.Gray,
                        leadingIconContentColor = if (type in filterState.selectedTypes)
                            type.accentColor
                        else
                            Color.Gray
                    ),
                    border = if (type in filterState.selectedTypes) {
                        BorderStroke(1.5.dp, type.accentColor)
                    } else {
                        BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    }
                )
            }
        }

        // === ROW 2: Options (Favoris, Pays, Tri) ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip Favoris
            AssistChip(
                onClick = onFavoritesToggle,
                label = {
                    Text(
                        "Favoris",
                        fontWeight = if (filterState.showFavoritesOnly)
                            FontWeight.Bold
                        else
                            FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (filterState.showFavoritesOnly)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (filterState.showFavoritesOnly)
                        Color(0xFFFFE5EC)
                    else
                        Color.White,
                    labelColor = if (filterState.showFavoritesOnly)
                        Color(0xFFE91E63)
                    else
                        Color.Gray,
                    leadingIconContentColor = if (filterState.showFavoritesOnly)
                        Color(0xFFE91E63)
                    else
                        Color.Gray
                ),
                border = if (filterState.showFavoritesOnly) {
                    BorderStroke(1.5.dp, Color(0xFFE91E63))
                } else {
                    BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                }
            )

            // Chip Epargne (optionnel)
            if (filterState.showFavoritesOnly) {
                AssistChip(
                    onClick = { },
                    label = { Text("Epargne", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Savings,
                            null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White,
                        labelColor = Color.Gray,
                        leadingIconContentColor = Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                )
            }

            // === PAYS AVEC DRAPEAUX ===
            availableCountries.take(2).forEach { country ->
                val flag = countryFlags[country] ?: "🌍"

                AssistChip(
                    onClick = { onCountryToggle(country) },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                flag,
                                fontSize = 16.sp
                            )
                            Text(
                                country,
                                fontWeight = if (country in filterState.selectedCountries)
                                    FontWeight.Bold
                                else
                                    FontWeight.Medium
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (country in filterState.selectedCountries)
                            Primary.copy(alpha = 0.15f)
                        else
                            Color.White,
                        labelColor = if (country in filterState.selectedCountries)
                            Primary
                        else
                            Color.Gray
                    ),
                    border = if (country in filterState.selectedCountries) {
                        BorderStroke(1.5.dp, Primary)
                    } else {
                        BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    }
                )
            }

            // Autre option (autre pays)
            if (availableCountries.size > 2) {
                AssistChip(
                    onClick = { },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("🌍", fontSize = 16.sp)
                            Text("Autre", fontWeight = FontWeight.Medium)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White,
                        labelColor = Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                )
            }

            // Bouton Clear All
            if (filterState.selectedTypes.isNotEmpty() ||
                filterState.selectedCountries.isNotEmpty() ||
                filterState.showFavoritesOnly
            ) {
                AssistChip(
                    onClick = onClearFilters,
                    label = {
                        Text(
                            "Effacer",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Close,
                            null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.3f),
                        labelColor = MaterialTheme.colorScheme.error,
                        leadingIconContentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}
