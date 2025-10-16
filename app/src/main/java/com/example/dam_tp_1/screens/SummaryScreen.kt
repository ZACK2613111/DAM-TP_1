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
import androidx.compose.ui.draw.clip
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
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
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

    Box(Modifier.fillMaxSize().background(Color.White)) {
        Scaffold(
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // === HEADER SUCCESS ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(0.1f)),
                    border = BorderStroke(2.dp, Color(0xFF4CAF50).copy(0.3f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            Modifier.size(80.dp).shadow(12.dp, CircleShape).background(Color(0xFF4CAF50), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Récapitulatif", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(8.dp))
                        Text("Vérifiez avant validation", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }

                // === INFOS DE BASE ===
                SummaryCard(title = "Informations", icon = Icons.Default.Info, accentColor = formData.selectedType.accentColor) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = formData.selectedType.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(16.dp)).border(2.dp, formData.selectedType.accentColor, RoundedCornerShape(16.dp)).padding(12.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(formData.productName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.height(4.dp))
                            Text(formData.selectedType.displayName, style = MaterialTheme.typography.bodyMedium, color = formData.selectedType.accentColor)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("📅 Date", formData.purchaseDate)
                    if (formData.country.isNotBlank()) SummaryRow("🌍 Pays", formData.country)
                }

                // === DÉTAILS COMMERCIAUX ===
                SummaryCard(title = "Détails", icon = Icons.Default.Store, accentColor = formData.selectedType.accentColor) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).background(Color(formData.selectedColorArgb), CircleShape).border(2.dp, Color.Gray.copy(0.3f), CircleShape))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Couleur", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            Text("#${Color(formData.selectedColorArgb).value.toString(16).uppercase().takeLast(6)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("🏷️ Marque", formData.brand)
                    SummaryRow("💰 Prix", "${formData.price} €")
                    if (formData.productSize.isNotBlank()) SummaryRow("📏 Taille", formData.productSize)
                    SummaryRow("🔧 État", formData.condition.displayName)
                }

                // === PRÉFÉRENCES ===
                SummaryCard(title = "Préférences", icon = Icons.Default.Star, accentColor = formData.selectedType.accentColor) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐ Favoris", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        if (formData.isFavorite) Icon(Icons.Default.Favorite, null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
                        else Text("Non", color = Color.Gray)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐ Note", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Row {
                            repeat(5) { star ->
                                Icon(if (star < formData.rating) Icons.Default.Star else Icons.Default.StarBorder, null, tint = if (star < formData.rating) Color(0xFFFFB300) else Color.Gray.copy(0.3f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    if (formData.hasWarranty) {
                        Spacer(Modifier.height(8.dp))
                        SummaryRow("🛡️ Garantie", formData.warrantyDuration.ifBlank { "Oui" })
                    }
                    if (formData.notes.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Text("📝 Notes", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(4.dp))
                        Text(formData.notes, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // === BUTTONS ===
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(2.dp, Color.Gray.copy(0.3f))) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Modifier", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {
                            viewModel.addProduct()
                            showSuccessDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(formData.selectedType.accentColor, formData.selectedType.accentColor.copy(0.8f))), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Save, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Enregistrer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            // === SUCCESS DIALOG ===
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    icon = {
                        Box(Modifier.size(64.dp).background(Color(0xFF4CAF50).copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                        }
                    },
                    title = { Text("Produit enregistré !", fontWeight = FontWeight.Bold) },
                    text = { Text("\"${formData.productName}\" a été ajouté avec succès !", color = Color.Gray) },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccessDialog = false
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }) {
                            Text("Voir ma collection", color = formData.selectedType.accentColor, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showSuccessDialog = false
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }) {
                            Text("Parfait", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color, content: @Composable ColumnScope.() -> Unit) {
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
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
    }
}
