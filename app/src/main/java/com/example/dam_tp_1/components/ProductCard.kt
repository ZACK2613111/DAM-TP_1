package com.example.dam_tp_1.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.dam_tp_1.data.ProductFormData

@Composable
fun ProductCard(
    product: ProductFormData,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable {
                onClick()
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // === HEADER ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image avec gradient border
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    product.selectedType.accentColor.copy(alpha = 0.2f),
                                    product.selectedType.accentColor.copy(alpha = 0.05f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            2.dp,
                            product.selectedType.accentColor,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = product.selectedType.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                            .padding(4.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Infos principales
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF1C1B1F)
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = product.selectedType.accentColor.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                product.selectedType.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = product.selectedType.accentColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (product.isFavorite) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Favorite,
                                null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Brand avec couleur
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.brand,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.Gray
                        )

                        if (product.selectedColorArgb != 0) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color(product.selectedColorArgb), CircleShape)
                                    .border(1.5.dp, Color.Gray.copy(0.3f), CircleShape)
                            )
                        }
                    }
                }

                // Delete button
                IconButton(
                    onClick = {
                        onDelete()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

            Spacer(Modifier.height(16.dp))

            // === RATING ET PRIX ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < product.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < product.rating) Color(0xFFFFB300) else Color.Gray.copy(0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${product.rating}/5",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray
                    )
                }

                // Prix avec gradient
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        product.selectedType.accentColor.copy(alpha = 0.2f),
                                        product.selectedType.accentColor.copy(alpha = 0.1f)
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "${product.price} €",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = product.selectedType.accentColor
                        )
                    }
                }
            }

            // === METADATA ===
            if (product.notes.isNotBlank() || product.country.isNotBlank()) {
                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Gray.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (product.country.isNotBlank()) {
                                Icon(
                                    Icons.Default.Public,
                                    null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Gray
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    product.country,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Spacer(Modifier.width(16.dp))
                            }

                            Icon(
                                Icons.Default.CalendarToday,
                                null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                product.purchaseDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        if (product.notes.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                product.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
