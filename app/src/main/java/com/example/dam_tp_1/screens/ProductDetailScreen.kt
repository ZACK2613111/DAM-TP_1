package com.example.dam_tp_1.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
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

    if (product == null) {
        Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(80.dp).shadow(12.dp, CircleShape).background(MaterialTheme.colorScheme.error.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Error, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))
                Text("Produit non trouvé", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Retour", color = Color.White)
                }
            }
        }
        return
    }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = { Text(product.productName, maxLines = 1, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF1C1B1F)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Retour", tint = Primary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Supprimer", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(paddingValues).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // === HERO SECTION ===
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = product.selectedType.accentColor.copy(0.1f)), border = BorderStroke(2.dp, product.selectedType.accentColor.copy(0.3f))) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Box(
                            Modifier.size(120.dp).shadow(12.dp, RoundedCornerShape(20.dp)).background(
                                Brush.verticalGradient(listOf(product.selectedType.accentColor.copy(0.2f), product.selectedType.accentColor.copy(0.05f))),
                                RoundedCornerShape(20.dp)
                            ).border(3.dp, product.selectedType.accentColor, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painter = painterResource(id = product.selectedType.imageRes), contentDescription = null, modifier = Modifier.size(70.dp).padding(8.dp))
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(product.productName, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF1C1B1F))
                        Spacer(Modifier.height(8.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = product.selectedType.accentColor), shape = RoundedCornerShape(12.dp)) {
                            Text(product.selectedType.displayName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { star ->
                                    Icon(if (star < product.rating) Icons.Default.Star else Icons.Default.StarBorder, null, tint = if (star < product.rating) Color(0xFFFFB300) else Color.Gray.copy(0.3f), modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("${product.rating}/5", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }

                            if (product.isFavorite) {
                                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE5EC)), shape = RoundedCornerShape(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                        Icon(Icons.Default.Favorite, null, tint = Color(0xFFE91E63), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Favori", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFE91E63))
                                    }
                                }
                            }
                        }
                    }
                }

                // === SECTIONS ===
                DetailSection(title = "Détails commerciaux", icon = Icons.Default.Store, accentColor = product.selectedType.accentColor) {
                    DetailRow(label = "Marque", value = product.brand, icon = Icons.Default.Storefront)
                    DetailRow(label = "Prix", value = "${product.price} €", icon = Icons.Default.Euro)
                    if (product.productSize.isNotBlank()) DetailRow(label = "Taille", value = product.productSize, icon = Icons.Default.Straighten)
                    DetailRow(label = "État", value = product.condition.displayName, icon = Icons.Default.VerifiedUser)
                }

                DetailSection(title = "Informations d'achat", icon = Icons.Default.ShoppingCart, accentColor = product.selectedType.accentColor) {
                    DetailRow(label = "Date d'achat", value = product.purchaseDate, icon = Icons.Default.CalendarToday)
                    if (product.country.isNotBlank()) DetailRow(label = "Pays", value = product.country, icon = Icons.Default.Language)
                }

                DetailSection(title = "Caractéristiques", icon = Icons.Default.Palette, accentColor = product.selectedType.accentColor) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Spacer(Modifier.width(12.dp))
                        Text("Couleur", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(28.dp).background(Color(product.selectedColorArgb), CircleShape).border(2.dp, Color.Gray.copy(0.3f), CircleShape))
                            Spacer(Modifier.width(8.dp))
                            Text("#${Color(product.selectedColorArgb).value.toString(16).uppercase().takeLast(6)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                if (product.hasWarranty) {
                    DetailSection(title = "Garantie", icon = Icons.Default.Security, accentColor = product.selectedType.accentColor) {
                        DetailRow(label = "Durée", value = product.warrantyDuration.ifBlank { "Garantie active" }, icon = Icons.Default.Schedule)
                    }
                }

                if (product.notes.isNotBlank()) {
                    DetailSection(title = "Notes personnelles", icon = Icons.Default.Notes, accentColor = product.selectedType.accentColor) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(0.05f)), shape = RoundedCornerShape(12.dp)) {
                            Text(product.notes, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            // === DELETE DIALOG ===
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    icon = {
                        Box(Modifier.size(64.dp).background(MaterialTheme.colorScheme.error.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
                        }
                    },
                    title = { Text("Supprimer le produit ?", fontWeight = FontWeight.Bold) },
                    text = { Text("\"${product.productName}\" sera définitivement supprimé.", color = Color.Gray) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.removeProduct(product)
                                showDeleteDialog = false
                                navController.popBackStack()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Supprimer", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Annuler", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Gray.copy(0.1f))) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.size(40.dp).background(accentColor.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
    }
}
