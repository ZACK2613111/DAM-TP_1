package com.example.dam_tp_1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_tp_1.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTopBar(
    totalProducts: Int,
    filteredCount: Int,
    onUserMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Ma Collection",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF1C1B1F)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "$filteredCount produit${if (filteredCount > 1) "s" else ""}" +
                            if (filteredCount != totalProducts) " sur $totalProducts" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        },
        actions = {
            IconButton(
                onClick = onUserMenuClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(6.dp, CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Primary, PrimaryContainer)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        "Menu utilisateur",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}
