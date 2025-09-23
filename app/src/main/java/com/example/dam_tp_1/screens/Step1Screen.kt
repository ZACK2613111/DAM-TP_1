package com.example.dam_tp_1.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dam_tp_1.components.StepHeader
import com.example.dam_tp_1.model.ProductType
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    navController: NavController,
    viewModel: ProductFormViewModel = viewModel()
) {
    val formData = viewModel.formData
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var imageScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = imageScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.updateFormData {
                            it.copy(purchaseDate = dateFormatter.format(Date(millis)))
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header avec progress
        StepHeader(
            stepNumber = 1,
            totalSteps = 3,
            title = "Informations de base",
            subtitle = "Type et détails principaux"
        )

        // Type de produit
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = formData.selectedType.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Type de produit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                AnimatedContent(
                    targetState = formData.selectedType,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
                    }
                ) { type ->
                    Image(
                        painter = painterResource(id = type.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .scale(animatedScale)
                            .border(
                                width = 3.dp,
                                color = type.accentColor,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProductType.entries.forEach { type ->
                        Card(
                            modifier = Modifier
                                .clickable {
                                    viewModel.updateFormData {
                                        it.copy(selectedType = type)
                                    }
                                    imageScale = 0.8f
                                    imageScale = 1.2f
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (type == formData.selectedType)
                                    type.accentColor.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = if (type == formData.selectedType)
                                BorderStroke(2.dp, type.accentColor)
                            else null
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                RadioButton(
                                    selected = type == formData.selectedType,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = type.accentColor
                                    )
                                )
                                Text(
                                    type.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (type == formData.selectedType)
                                        type.accentColor
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Informations produit
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
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = formData.selectedType.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Informations produit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = formData.productName,
                    onValueChange = { newName ->
                        viewModel.updateFormData { it.copy(productName = newName) }
                    },
                    label = { Text("Nom du produit *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = formData.selectedType.accentColor
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = formData.selectedType.accentColor,
                        focusedLabelColor = formData.selectedType.accentColor
                    )
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = formData.purchaseDate,
                    onValueChange = {},
                    label = { Text("Date d'achat *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = formData.selectedType.accentColor
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Sélectionner la date",
                                tint = formData.selectedType.accentColor
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = formData.country,
                    onValueChange = { newCountry ->
                        viewModel.updateFormData { it.copy(country = newCountry) }
                    },
                    label = { Text("Pays d'origine") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = formData.selectedType.accentColor
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = formData.selectedType.accentColor,
                        focusedLabelColor = formData.selectedType.accentColor
                    )
                )
            }
        }

        // Bouton Suivant
        ElevatedButton(
            onClick = {
                if (viewModel.isStep1Valid()) {
                    navController.navigate(Screen.Step2.route)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = formData.selectedType.accentColor,
                contentColor = Color.White
            ),
            enabled = viewModel.isStep1Valid()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Suivant",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
