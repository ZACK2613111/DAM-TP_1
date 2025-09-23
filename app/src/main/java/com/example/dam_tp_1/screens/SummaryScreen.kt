package com.example.dam_tp_1.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import kotlinx.coroutines.launch

@Composable
fun SummaryScreen(
    navController: NavController,
    viewModel: ProductFormViewModel
) {
    val formData = viewModel.formData
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header avec animation de succès
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "🎉 Récapitulatif",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "Vérifiez les informations avant validation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Informations de base
            SummaryCard(
                title = "Informations de base",
                icon = Icons.Default.Info,
                accentColor = formData.selectedType.accentColor
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = formData.selectedType.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    2.dp,
                                    formData.selectedType.accentColor,
                                    MaterialTheme.shapes.medium
                                )
                                .padding(8.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                formData.productName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                formData.selectedType.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = formData.selectedType.accentColor
                            )
                        }
                    }

                    SummaryRow("📅 Date d'achat", formData.purchaseDate)
                    if (formData.country.isNotBlank()) {
                        SummaryRow("🌍 Pays", formData.country)
                    }
                }
            }

            // Détails commerciaux
            SummaryCard(
                title = "Détails commerciaux",
                icon = Icons.Default.Store,
                accentColor = formData.selectedType.accentColor
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(formData.selectedColorArgb),
                                    CircleShape
                                )
                                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Couleur sélectionnée", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "#${Color(formData.selectedColorArgb).value.toString(16).uppercase().takeLast(6)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    SummaryRow("🏷️ Marque", formData.brand)
                    SummaryRow("💰 Prix", "${formData.price} €")
                    if (formData.productSize.isNotBlank()) {
                        SummaryRow("📏 Taille", formData.productSize)
                    }
                    SummaryRow("🔧 État", formData.condition.displayName)
                }
            }

            // Préférences
            SummaryCard(
                title = "Préférences",
                icon = Icons.Default.Star,
                accentColor = formData.selectedType.accentColor
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⭐ Favoris", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        if (formData.isFavorite) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Non", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⭐ Note", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Row {
                            repeat(5) { star ->
                                Icon(
                                    imageVector = if (star < formData.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (star < formData.rating) Color(0xFFFFA726) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (formData.hasWarranty) {
                        SummaryRow("🛡️ Garantie", formData.warrantyDuration.ifBlank { "Oui" })
                    }

                    if (formData.notes.isNotBlank()) {
                        HorizontalDivider()
                        Text("📝 Notes :", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text(
                            formData.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Modifier")
                }

                ElevatedButton(
                    onClick = {
                        // ✅ AJOUT DU PRODUIT À LA COLLECTION
                        viewModel.addProduct()
                        showSuccessDialog = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = formData.selectedType.accentColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Enregistrer")
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Dialog de succès
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        "🎉 Produit enregistré !",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text("Votre produit \"${formData.productName}\" a été ajouté avec succès à votre collection !")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            // ✅ NAVIGATION VERS LA PAGE D'ACCUEIL
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Produit ajouté à votre collection !")
                            }
                        }
                    ) {
                        Text("🏠 Voir ma collection", color = formData.selectedType.accentColor)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            // ✅ RETOUR À L'ACCUEIL AUSSI
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    ) {
                        Text("👍 Parfait")
                    }
                }
            )
        }
    }
}

@Composable
private fun SummaryCard(
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
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
