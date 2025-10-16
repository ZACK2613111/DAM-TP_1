package com.example.dam_tp_1.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_tp_1.ui.theme.*

@Composable
fun EmptyCollectionState(
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // === ICON AVEC GRADIENT ===
        Box(
            modifier = Modifier
                .size(140.dp)
                .shadow(16.dp, CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Primary.copy(0.2f), Primary.copy(0.05f))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Inventory,
                null,
                modifier = Modifier.size(70.dp),
                tint = Primary
            )
        }

        Spacer(Modifier.height(40.dp))

        Text(
            "Votre collection est vide",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp
            ),
            textAlign = TextAlign.Center,
            color = Color(0xFF1C1B1F)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Commencez à construire votre collection\nen ajoutant votre premier produit",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(Modifier.height(48.dp))

        // === BOUTON AJOUTER PREMIUM ===
        Button(
            onClick = onAddProduct,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(listOf(Primary, PrimaryContainer)),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Ajouter mon premier produit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(56.dp))

        // === FEATURES PREVIEW ===
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            FeaturePreview(Icons.Default.Search, "Recherche")
            FeaturePreview(Icons.Default.Star, "Favoris")
            FeaturePreview(Icons.Default.Analytics, "Stats")
        }
    }
}

@Composable
private fun FeaturePreview(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(6.dp, CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.15f),
                            Primary.copy(alpha = 0.05f)
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = Primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.Gray
        )
    }
}
