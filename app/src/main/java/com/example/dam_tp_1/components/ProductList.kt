package com.example.dam_tp_1.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.ui.theme.*

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            IconButton(
                onClick = { onPageChange(currentPage - 1) },
                enabled = currentPage > 0,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (currentPage > 0) Primary.copy(0.1f) else Color.Gray.copy(0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    "Page précédente",
                    tint = if (currentPage > 0) Primary else Color.Gray
                )
            }

            // Page indicator
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(Primary, PrimaryContainer)),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "${currentPage + 1} / $totalPages",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }

            // Next button
            IconButton(
                onClick = { onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages - 1,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (currentPage < totalPages - 1) Primary.copy(0.1f) else Color.Gray.copy(0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    "Page suivante",
                    tint = if (currentPage < totalPages - 1) Primary else Color.Gray
                )
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Analytics,
                    null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Aperçu de votre collection",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color(0xFF1C1B1F)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Inventory,
                    value = productsList.size.toString(),
                    label = "Produits",
                    color = Primary
                )
                StatItem(
                    icon = Icons.Default.Favorite,
                    value = favoriteCount.toString(),
                    label = "Favoris",
                    color = Color(0xFFE91E63)
                )
                StatItem(
                    icon = Icons.Default.Euro,
                    value = "${totalValue.toInt()}",
                    label = "Valeur",
                    color = Color(0xFF4CAF50)
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color.copy(0.2f), color.copy(0.05f))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = color
        )

        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.Gray
        )
    }
}
