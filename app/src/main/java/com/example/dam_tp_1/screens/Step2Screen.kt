package com.example.dam_tp_1.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import com.example.dam_tp_1.components.StepHeader
import com.example.dam_tp_1.data.ProductCondition
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import com.github.skydoves.colorpicker.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    navController: NavController,
    viewModel: ProductFormViewModel
) {
    val formData = viewModel.formData
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    var showColorPicker by remember { mutableStateOf(false) }
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
                            "Étape 2/3",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (formData.brand.isNotBlank() || formData.price.isNotBlank()) {
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
                    stepNumber = 2,
                    totalSteps = 3,
                    title = "Apparence & Détails",
                    subtitle = "Caractéristiques visuelles"
                )

                // === COULEUR ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
                ) {
                    Column(Modifier.padding(24.dp)) {
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
                                Icon(Icons.Default.Palette, null, tint = formData.selectedType.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Couleur du produit", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                            Spacer(Modifier.weight(1f))
                            Box(
                                Modifier
                                    .size(56.dp)
                                    .shadow(6.dp, CircleShape)
                                    .background(Color(formData.selectedColorArgb), CircleShape)
                                    .clickable { showColorPicker = !showColorPicker },
                                contentAlignment = Alignment.Center
                            ) {
                                if (showColorPicker) {
                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = showColorPicker,
                            enter = slideInVertically() + expandVertically() + fadeIn(),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut()
                        ) {
                            Column(Modifier.padding(top = 20.dp)) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(0.05f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    HsvColorPicker(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(280.dp)
                                            .padding(16.dp),
                                        controller = rememberColorPickerController(),
                                        onColorChanged = { colorEnvelope ->
                                            viewModel.updateSelectedColor(colorEnvelope.color.toArgb())
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // === DÉTAILS COMMERCIAUX ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(formData.selectedType.accentColor.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, null, tint = formData.selectedType.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Détails commerciaux", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                        }

                        Spacer(Modifier.height(20.dp))

                        OutlinedTextField(
                            value = formData.brand,
                            onValueChange = viewModel::updateBrand,
                            label = { Text("Marque *") },
                            leadingIcon = { Icon(Icons.Default.Storefront, null, tint = formData.selectedType.accentColor) },
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

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = formData.price,
                            onValueChange = viewModel::updatePrice,
                            label = { Text("Prix d'achat *") },
                            leadingIcon = { Icon(Icons.Default.Euro, null, tint = formData.selectedType.accentColor) },
                            suffix = { Text("€", fontWeight = FontWeight.Bold) },
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

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = formData.productSize,
                            onValueChange = viewModel::updateProductSize,
                            label = { Text("Taille/Dimension") },
                            leadingIcon = { Icon(Icons.Default.Straighten, null, tint = formData.selectedType.accentColor) },
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

                // === ÉTAT DU PRODUIT ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(formData.selectedType.accentColor.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VerifiedUser, null, tint = formData.selectedType.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("État du produit", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                        }

                        Spacer(Modifier.height(16.dp))

                        ProductCondition.entries.forEach { condition ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateFormData { it.copy(condition = condition) }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = condition == formData.condition,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = formData.selectedType.accentColor)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(condition.displayName, fontWeight = if (condition == formData.condition) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
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
                            if (viewModel.isStep2Valid()) {
                                navController.navigate(Screen.Step3.route)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = viewModel.isStep2Valid()
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(
                                    if (viewModel.isStep2Valid())
                                        Brush.horizontalGradient(listOf(formData.selectedType.accentColor, formData.selectedType.accentColor.copy(0.8f)))
                                    else
                                        Brush.horizontalGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f))),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Suivant", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, null, tint = Color.White, modifier = Modifier.size(20.dp))
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
