package com.example.dam_tp_1.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dam_tp_1.components.StepHeader
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.viewmodel.ProductFormViewModel

@Composable
fun Step3Screen(
    navController: NavController,
    viewModel: ProductFormViewModel = viewModel()
) {
    val formData = viewModel.formData
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StepHeader(
            stepNumber = 3,
            totalSteps = 3,
            title = "Préférences finales",
            subtitle = "Derniers détails"
        )

        // Favoris et évaluation
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
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = formData.selectedType.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Évaluation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Checkbox Favoris
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateFormData { it.copy(isFavorite = !it.isFavorite) }
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (formData.isFavorite)
                            formData.selectedType.accentColor.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
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
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                "Ce produit apparaîtra dans vos favoris",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Rating
                Text("Note du produit", style = MaterialTheme.typography.bodyLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = {
                                viewModel.updateFormData { it.copy(rating = star) }
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        ) {
                            Icon(
                                imageVector = if (star <= formData.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Note $star",
                                tint = if (star <= formData.rating) Color(0xFFFFA726) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Garantie
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateFormData {
                                it.copy(hasWarranty = !it.hasWarranty)
                            }
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
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = formData.selectedType.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Le produit a une garantie",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (formData.hasWarranty) {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = formData.warrantyDuration,
                        onValueChange = { newWarranty ->
                            viewModel.updateFormData { it.copy(warrantyDuration = newWarranty) }
                        },
                        label = { Text("Durée de garantie") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = formData.selectedType.accentColor
                            )
                        },
                        placeholder = { Text("Ex: 2 ans, 24 mois...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = formData.selectedType.accentColor,
                            focusedLabelColor = formData.selectedType.accentColor
                        )
                    )
                }
            }
        }

        // Notes
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
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = formData.selectedType.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Notes personnelles",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = formData.notes,
                    onValueChange = { newNotes ->
                        viewModel.updateFormData { it.copy(notes = newNotes) }
                    },
                    label = { Text("Remarques, commentaires...") },
                    placeholder = { Text("Ajoutez des notes sur ce produit...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = formData.selectedType.accentColor,
                        focusedLabelColor = formData.selectedType.accentColor
                    )
                )
            }
        }

        // Navigation buttons
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Retour")
            }

            ElevatedButton(
                onClick = {
                    navController.navigate(Screen.Summary.route)
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
                Text("Terminer")
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
