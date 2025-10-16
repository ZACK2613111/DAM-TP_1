package com.example.dam_tp_1.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dam_tp_1.data.ProductFormData

@Composable
fun ProductList(
    products: List<ProductFormData>,
    allProducts: List<ProductFormData>,
    currentPage: Int,
    totalPages: Int,
    itemsPerPage: Int,
    showStats: Boolean,
    onProductClick: (Int) -> Unit,
    onProductDelete: (ProductFormData) -> Unit,
    onPageChange: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stats header
        if (showStats) {
            item {
                StatsHeader(allProducts)
            }
        }

        // Product cards
        itemsIndexed(
            items = products,
            key = { index, product -> "${product.productName}${product.brand}$index" }
        ) { index, product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(index) },
                onDelete = { onProductDelete(product) }
            )
        }

        // Pagination
        if (totalPages > 1) {
            item {
                PaginationControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPageChange = onPageChange
                )
            }
        }

        // Spacer for FAB
        item {
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPageChange(currentPage - 1) },
                enabled = currentPage > 0
            ) {
                Icon(Icons.Default.ChevronLeft, "Page précédente")
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "${currentPage + 1} / $totalPages",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            IconButton(
                onClick = { onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages - 1
            ) {
                Icon(Icons.Default.ChevronRight, "Page suivante")
            }
        }
    }
}

@Composable
private fun StatsHeader(productsList: List<ProductFormData>) {
    val favoriteCount = productsList.count { it.isFavorite }
    val totalValue = productsList.sumOf { it.price.toDoubleOrNull() ?: 0.0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Aperçu de votre collection",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Inventory,
                    value = productsList.size.toString(),
                    label = "Produits",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.Favorite,
                    value = favoriteCount.toString(),
                    label = "Favoris",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.Euro,
                    value = "${totalValue.toInt()}",
                    label = "Valeur",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}
