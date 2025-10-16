package com.example.dam_tp_1.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_tp_1.ui.theme.*

@Composable
fun NoResultsState(
    onClearFilters: () -> Unit,
    onAddProduct: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(12.dp, CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Primary.copy(alpha = 0.2f), Primary.copy(alpha = 0.05f))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    null,
                    modifier = Modifier.size(60.dp),
                    tint = Primary
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "Aucun produit trouvé",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color(0xFF1C1B1F),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Essayez de modifier vos critères de recherche\nou ajoutez de nouveaux produits à votre collection",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(40.dp))

            // Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                // Clear Filters Button
                Button(
                    onClick = onClearFilters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(Primary, PrimaryContainer)),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ClearAll, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Effacer les filtres",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                // Add Product Button
                OutlinedButton(
                    onClick = onAddProduct,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Ajouter un produit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
