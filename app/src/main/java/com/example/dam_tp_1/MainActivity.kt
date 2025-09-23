package com.example.dam_tp_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.dam_tp_1.model.ProductType
import com.example.dam_tp_1.ui.theme.DAMTP_1Theme
import com.github.skydoves.colorpicker.compose.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DAMTP_1Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ProductFormPremium()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormPremium() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

    // States
    var selectedType by remember { mutableStateOf(ProductType.Consumable) }
    var productName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFF6750A4)) }
    var isFavorite by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    var triedSubmit by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Animation states
    var imageScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = imageScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    fun resetForm() {
        productName = ""
        country = ""
        purchaseDate = ""
        selectedColor = Color(0xFF6750A4)
        isFavorite = false
        triedSubmit = false
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun validateAndSubmit() {
        triedSubmit = true
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

        if (productName.isBlank() || purchaseDate.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "🚫 Veuillez remplir tous les champs obligatoires",
                    duration = SnackbarDuration.Short
                )
            }
        } else {
            showConfirmationDialog = true
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        purchaseDate = dateFormatter.format(Date(it))
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header avec gradient et animation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = selectedType.accentColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "✨ Nouveau Produit",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = selectedType.accentColor
                        )
                        Text(
                            text = "Ajoutez votre produit en quelques étapes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Section Type avec animations
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
                                tint = selectedType.accentColor,
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

                        // Image animée
                        AnimatedContent(
                            targetState = selectedType,
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
                                    .clip(MaterialTheme.shapes.medium)
                                    .border(
                                        width = 3.dp,
                                        color = type.accentColor,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(8.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Radio buttons modernes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProductType.entries.forEach { type ->
                                Card(
                                    modifier = Modifier
                                        .clickable {
                                            selectedType = type
                                            imageScale = 0.8f
                                            imageScale = 1.2f
                                            resetForm()
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (type == selectedType)
                                            type.accentColor.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (type == selectedType)
                                        BorderStroke(2.dp, type.accentColor)
                                    else null
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        RadioButton(
                                            selected = type == selectedType,
                                            onClick = null,
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = type.accentColor
                                            )
                                        )
                                        Text(
                                            type.displayName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (type == selectedType)
                                                type.accentColor
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Informations
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
                                tint = selectedType.accentColor,
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

                        // Nom du produit avec icône
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Nom du produit *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = selectedType.accentColor
                                )
                            },
                            isError = triedSubmit && productName.isBlank(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = selectedType.accentColor,
                                focusedLabelColor = selectedType.accentColor
                            ),
                            supportingText = {
                                if (triedSubmit && productName.isBlank()) {
                                    Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )

                        Spacer(Modifier.height(16.dp))

                        // Date avec animation
                        OutlinedTextField(
                            value = purchaseDate,
                            onValueChange = {},
                            label = { Text("Date d'achat *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = selectedType.accentColor
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Sélectionner la date",
                                        tint = selectedType.accentColor
                                    )
                                }
                            },
                            isError = triedSubmit && purchaseDate.isBlank(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = if (triedSubmit && purchaseDate.isBlank())
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.outline
                            ),
                            supportingText = {
                                if (triedSubmit && purchaseDate.isBlank()) {
                                    Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )

                        Spacer(Modifier.height(16.dp))

                        // Pays
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Pays d'origine") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = selectedType.accentColor
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = selectedType.accentColor,
                                focusedLabelColor = selectedType.accentColor
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        // ColorPicker moderne
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = selectedType.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Couleur du produit", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.weight(1f))
                            Card(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable { showColorPicker = !showColorPicker },
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = selectedColor)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (showColorPicker) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Fermer",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // ColorPicker animé
                        AnimatedVisibility(
                            visible = showColorPicker,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                HsvColorPicker(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .padding(16.dp),
                                    controller = rememberColorPickerController(),
                                    onColorChanged = { colorEnvelope ->
                                        selectedColor = colorEnvelope.color
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Checkbox Favoris avec animation
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isFavorite = !isFavorite
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFavorite)
                                    selectedType.accentColor.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Checkbox(
                                    checked = isFavorite,
                                    onCheckedChange = { isFavorite = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = selectedType.accentColor
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
                    }
                }

                // Bouton de validation moderne avec animation
                ElevatedButton(
                    onClick = { validateAndSubmit() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = selectedType.accentColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Valider le produit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            // Dialogs modernes
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QuestionMark,
                            contentDescription = null,
                            tint = selectedType.accentColor
                        )
                    },
                    title = {
                        Text(
                            "Confirmer l'ajout ?",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text("Voulez-vous vraiment ajouter ce produit à votre liste ?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmationDialog = false
                                showInfoDialog = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        ) {
                            Text("✅ Oui", color = selectedType.accentColor)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmationDialog = false }) {
                            Text("❌ Non")
                        }
                    }
                )
            }

            if (showInfoDialog) {
                AlertDialog(
                    onDismissRequest = { showInfoDialog = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    },
                    title = {
                        Text(
                            "🎉 Produit ajouté !",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Column {
                            Text(
                                "Voici le résumé de votre produit :",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    InfoRow("📝 Nom", productName)
                                    InfoRow("🏷️ Type", selectedType.displayName)
                                    InfoRow("📅 Date d'achat", purchaseDate)
                                    InfoRow("🎨 Couleur", "#${selectedColor.toArgb().toHexString()}")
                                    if (country.isNotBlank()) InfoRow("🌍 Pays", country)
                                    InfoRow("⭐ Favoris", if (isFavorite) "Oui" else "Non")
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showInfoDialog = false }) {
                            Text("👍 Parfait !", color = selectedType.accentColor)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun Int.toHexString(): String = Integer.toHexString(this).uppercase().takeLast(6)
