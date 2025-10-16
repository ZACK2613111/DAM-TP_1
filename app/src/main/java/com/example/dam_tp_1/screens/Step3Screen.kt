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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import com.example.dam_tp_1.components.StepHeader
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.ProductFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3Screen(
    navController: NavController,
    viewModel: ProductFormViewModel
) {
    val formData = viewModel.formData
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    var showExitDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Étape 3/3",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (formData.notes.isNotBlank() || formData.warrantyDuration.isNotBlank()) {
                                showExitDialog = true
                            } else {
                                navController.popBackStack()
                            }
                        }) {
                            Icon(Icons.Default.Close, "Quitter", tint = Primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StepHeader(
                    stepNumber = 3,
                    totalSteps = 3,
                    title = "Préférences finales",
                    subtitle = "Derniers détails"
                )

                // === FAVORIS ET ÉVALUATION ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(formData.selectedType.accentColor.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Star, null, tint = formData.selectedType.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Évaluation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                        }

                        Spacer(Modifier.height(20.dp))

                        // Checkbox Favoris
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateFavorite(!formData.isFavorite)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (formData.isFavorite)
                                    formData.selectedType.accentColor.copy(alpha = 0.1f)
                                else
                                    Color.Gray.copy(0.05f)
                            ),
                            border = if (formData.isFavorite)
                                BorderStroke(2.dp, formData.selectedType.accentColor)
                            else
                                BorderStroke(1.dp, Color.Gray.copy(0.2f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Checkbox(
                                    checked = formData.isFavorite,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = formData.selectedType.accentColor
                                    )
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "⭐ Ajouter aux favoris",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        "Ce produit apparaîtra en haut",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Rating
                        Text("Note du produit", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = {
                                        viewModel.updateRating(star)
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            if (star <= formData.rating)
                                                Color(0xFFFFF8E1)
                                            else
                                                Color.Gray.copy(0.1f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (star <= formData.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Note $star",
                                        tint = if (star <= formData.rating) Color(0xFFFFB300) else Color.Gray.copy(0.4f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // === GARANTIE ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateWarranty(!formData.hasWarranty)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                        ) {
                            Checkbox(
                                checked = formData.hasWarranty,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = formData.selectedType.accentColor
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(formData.selectedType.accentColor.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, null, tint = formData.selectedType.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Le produit a une garantie",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (formData.hasWarranty) {
                            Spacer(Modifier.height(20.dp))
                            OutlinedTextField(
                                value = formData.warrantyDuration,
                                onValueChange = viewModel::updateWarrantyDuration,
                                label = { Text("Durée de garantie") },
                                leadingIcon = { Icon(Icons.Default.Schedule, null, tint = formData.selectedType.accentColor) },
                                placeholder = { Text("Ex: 2 ans, 24 mois...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = formData.selectedType.accentColor,
                                    focusedLabelColor = formData.selectedType.accentColor,
                                    unfocusedBorderColor = Color.LightGray.copy(0.5f),
                                    cursorColor = formData.selectedType.accentColor,
                                    focusedContainerColor = formData.selectedType.accentColor.copy(0.03f)
                                )
                            )
                        }
                    }
                }

                // === NOTES ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(formData.selectedType.accentColor.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notes, null, tint = formData.selectedType.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Notes personnelles",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1C1B1F)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        OutlinedTextField(
                            value = formData.notes,
                            onValueChange = viewModel::updateNotes,
                            label = { Text("Remarques, commentaires...") },
                            placeholder = { Text("Ajoutez des notes sur ce produit...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = formData.selectedType.accentColor,
                                focusedLabelColor = formData.selectedType.accentColor,
                                unfocusedBorderColor = Color.LightGray.copy(0.5f),
                                cursorColor = formData.selectedType.accentColor,
                                focusedContainerColor = formData.selectedType.accentColor.copy(0.03f)
                            )
                        )
                    }
                }

                // === NAVIGATION BUTTONS ===
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, Color.Gray.copy(0.3f))
                    ) {
                        Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Retour", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            navController.navigate(Screen.Summary.route)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(formData.selectedType.accentColor, formData.selectedType.accentColor.copy(0.8f))
                                    ),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Terminer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            // === EXIT DIALOG ===
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    icon = {
                        Box(
                            Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.error.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
                        }
                    },
                    title = { Text("Quitter le formulaire ?", fontWeight = FontWeight.Bold) },
                    text = { Text("Vos modifications seront perdues.", color = Color.Gray) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showExitDialog = false
                                viewModel.resetForm()
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Quitter", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("Continuer", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}
