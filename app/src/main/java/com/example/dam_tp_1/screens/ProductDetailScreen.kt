package com.example.dam_tp_1.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.ui.theme.*
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

    // Product not found state
    if (product == null) {
        ProductNotFoundScreen(onBack = { navController.popBackStack() })
        return
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ProductDetailTopBar(
                productName = product.productName,
                onBackClick = { navController.popBackStack() },
                onDeleteClick = { showDeleteDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Section with Image
            ProductHeroSection(product = product)

            // Main Information Card
            ProductMainInfoCard(product = product)

            // Purchase Information
            ProductPurchaseInfoCard(product = product)

            // Product Features
            ProductFeaturesCard(product = product)

            // Warranty Information
            if (product.hasWarranty) {
                ProductWarrantyCard(product = product)
            }

            // Personal Notes
            if (product.notes.isNotBlank()) {
                ProductNotesCard(notes = product.notes, accentColor = product.selectedType.accentColor)
            }

            Spacer(Modifier.height(16.dp))
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            DeleteProductDialog(
                productName = product.productName,
                onConfirm = {
                    viewModel.removeProduct(product)
                    showDeleteDialog = false
                    navController.popBackStack()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

// ========================================
// TOP BAR
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailTopBar(
    productName: String,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = productName,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color(0xFF1C1B1F)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Primary
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

// ========================================
// HERO SECTION
// ========================================

@Composable
private fun ProductHeroSection(product: ProductFormData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = product.selectedType.accentColor.copy(0.1f)
        ),
        border = BorderStroke(2.dp, product.selectedType.accentColor.copy(0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                product.selectedType.accentColor.copy(0.2f),
                                product.selectedType.accentColor.copy(0.05f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .border(3.dp, product.selectedType.accentColor, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (product.customImageUri != null) {
                    AsyncImage(
                        model = product.customImageUri,
                        contentDescription = "Image du produit",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(17.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = product.selectedType.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(8.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Product Name
            Text(
                text = product.productName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color(0xFF1C1B1F),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // Product Type Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = product.selectedType.accentColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = product.selectedType.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Rating and Favorite
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rating Stars
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < product.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index < product.rating) Color(0xFFFFB300) else Color.Gray.copy(0.3f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${product.rating}/5",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Favorite Badge
                if (product.isFavorite) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE5EC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Favori",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFE91E63)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========================================
// MAIN INFO CARD
// ========================================

@Composable
private fun ProductMainInfoCard(product: ProductFormData) {
    InfoCard(
        title = "Détails commerciaux",
        icon = Icons.Default.Store,
        accentColor = product.selectedType.accentColor
    ) {
        InfoRow(
            label = "Marque",
            value = product.brand,
            icon = Icons.Default.Storefront
        )

        InfoRow(
            label = "Prix",
            value = "${product.price} €",
            icon = Icons.Default.Euro,
            valueColor = product.selectedType.accentColor
        )

        if (product.productSize.isNotBlank()) {
            InfoRow(
                label = "Taille",
                value = product.productSize,
                icon = Icons.Default.Straighten
            )
        }

        InfoRow(
            label = "État",
            value = product.condition.displayName,
            icon = Icons.Default.VerifiedUser
        )
    }
}

// ========================================
// PURCHASE INFO CARD
// ========================================

@Composable
private fun ProductPurchaseInfoCard(product: ProductFormData) {
    InfoCard(
        title = "Informations d'achat",
        icon = Icons.Default.ShoppingCart,
        accentColor = product.selectedType.accentColor
    ) {
        InfoRow(
            label = "Date d'achat",
            value = product.purchaseDate,
            icon = Icons.Default.CalendarToday
        )

        if (product.country.isNotBlank()) {
            InfoRow(
                label = "Pays d'origine",
                value = product.country,
                icon = Icons.Default.Language
            )
        }
    }
}

// ========================================
// FEATURES CARD
// ========================================

@Composable
private fun ProductFeaturesCard(product: ProductFormData) {
    InfoCard(
        title = "Caractéristiques",
        icon = Icons.Default.Palette,
        accentColor = product.selectedType.accentColor
    ) {
        // Color Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "Couleur",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(product.selectedColorArgb), CircleShape)
                        .border(2.dp, Color.Gray.copy(0.3f), CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "#${Color(product.selectedColorArgb).value.toString(16).uppercase().takeLast(6)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1C1B1F)
                )
            }
        }
    }
}

// ========================================
// WARRANTY CARD
// ========================================

@Composable
private fun ProductWarrantyCard(product: ProductFormData) {
    InfoCard(
        title = "Garantie",
        icon = Icons.Default.Security,
        accentColor = Color(0xFF4CAF50)
    ) {
        InfoRow(
            label = "Durée de garantie",
            value = product.warrantyDuration.ifBlank { "Garantie active" },
            icon = Icons.Default.Schedule
        )
    }
}

// ========================================
// NOTES CARD
// ========================================

@Composable
private fun ProductNotesCard(notes: String, accentColor: Color) {
    InfoCard(
        title = "Notes personnelles",
        icon = Icons.Default.Notes,
        accentColor = accentColor
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(0.05f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1C1B1F),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// ========================================
// REUSABLE COMPONENTS
// ========================================

@Composable
private fun InfoCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray.copy(0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accentColor.copy(0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1C1B1F)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Content
            content()
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color = Color(0xFF1C1B1F)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Gray
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = valueColor
        )
    }
}

// ========================================
// PRODUCT NOT FOUND
// ========================================

@Composable
private fun ProductNotFoundScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(12.dp, CircleShape)
                    .background(
                        MaterialTheme.colorScheme.error.copy(0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Produit introuvable",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1C1B1F)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Ce produit n'existe plus",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Retour à l'accueil",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

// ========================================
// DELETE DIALOG
// ========================================

@Composable
private fun DeleteProductDialog(
    productName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.error.copy(0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Supprimer le produit ?",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "\"$productName\" sera définitivement supprimé.",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Supprimer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
