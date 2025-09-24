package com.example.dam_tp_1.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.viewmodel.ProductFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    viewModel: ProductFormViewModel,
    productId: Int
) {
    val product = viewModel.productsList.getOrNull(productId)
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Produit non trouvé",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(16.dp))
                ElevatedButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Retour")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        product.productName,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = product.selectedType.accentColor.copy(alpha = 0.1f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Section avec image
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = product.selectedType.accentColor.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    // Grande image du produit
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                product.selectedType.accentColor.copy(alpha = 0.2f),
                                MaterialTheme.shapes.large
                            )
                            .border(
                                3.dp,
                                product.selectedType.accentColor,
                                MaterialTheme.shapes.large
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = product.selectedType.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Nom et type
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = product.selectedType.accentColor
                        )
                    ) {
                        Text(
                            text = product.selectedType.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Rating et favoris
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { star ->
                                Icon(
                                    imageVector = if (star < product.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (star < product.rating) Color(0xFFFFA726) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${product.rating}/5",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (product.isFavorite) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Red.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Favori",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Informations détaillées
            DetailSection(
                title = "Informations commerciales",
                icon = Icons.Default.Store,
                accentColor = product.selectedType.accentColor
            ) {
                DetailRow(
                    label = "Marque",
                    value = product.brand,
                    icon = Icons.Default.Storefront
                )
                DetailRow(
                    label = "Prix d'achat",
                    value = "${product.price} €",
                    icon = Icons.Default.AttachMoney
                )
                if (product.productSize.isNotBlank()) {
                    DetailRow(
                        label = "Taille/Dimension",
                        value = product.productSize,
                        icon = Icons.Default.Straighten
                    )
                }
                DetailRow(
                    label = "État",
                    value = product.condition.displayName,
                    icon = Icons.Default.VerifiedUser
                )
            }

            // Informations d'achat
            DetailSection(
                title = "Informations d'achat",
                icon = Icons.Default.ShoppingCart,
                accentColor = product.selectedType.accentColor
            ) {
                DetailRow(
                    label = "Date d'achat",
                    value = product.purchaseDate,
                    icon = Icons.Default.CalendarToday
                )
                if (product.country.isNotBlank()) {
                    DetailRow(
                        label = "Pays d'origine",
                        value = product.country,
                        icon = Icons.Default.Public
                    )
                }
            }

            // Caractéristiques visuelles
            DetailSection(
                title = "Caractéristiques",
                icon = Icons.Default.Palette,
                accentColor = product.selectedType.accentColor
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Couleur",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    Color(product.selectedColorArgb),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "#${Color(product.selectedColorArgb).value.toString(16).uppercase().takeLast(6)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Garantie si applicable
            if (product.hasWarranty) {
                DetailSection(
                    title = "Garantie",
                    icon = Icons.Default.Security,
                    accentColor = product.selectedType.accentColor
                ) {
                    DetailRow(
                        label = "Durée de garantie",
                        value = product.warrantyDuration.ifBlank { "Garantie active" },
                        icon = Icons.Default.Schedule
                    )
                }
            }

            // Notes personnelles
            if (product.notes.isNotBlank()) {
                DetailSection(
                    title = "Notes personnelles",
                    icon = Icons.Default.Notes,
                    accentColor = product.selectedType.accentColor
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = product.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Dialog de suppression
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text("Supprimer le produit ?")
                },
                text = {
                    Text("Êtes-vous sûr de vouloir supprimer \"${product.productName}\" de votre collection ? Cette action est irréversible.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.removeProduct(product)
                            showDeleteDialog = false
                            navController.popBackStack()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
