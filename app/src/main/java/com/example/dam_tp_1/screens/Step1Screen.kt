package com.example.dam_tp_1.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import com.example.dam_tp_1.components.StepHeader
import com.example.dam_tp_1.components.ProductImagePicker
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import java.text.SimpleDateFormat
import java.util.*

// === HELPER FUNCTION: Countries with Flags ===
private fun getCountryFlags() = mapOf(
    "France" to "🇫🇷",
    "Algérie" to "🇩🇿",
    "Maroc" to "🇲🇦",
    "Tunisie" to "🇹🇳",
    "Canada" to "🇨🇦",
    "États-Unis" to "🇺🇸",
    "Royaume-Uni" to "🇬🇧",
    "Allemagne" to "🇩🇪",
    "Italie" to "🇮🇹",
    "Espagne" to "🇪🇸",
    "Belgique" to "🇧🇪",
    "Suisse" to "🇨🇭",
    "Portugal" to "🇵🇹",
    "Pays-Bas" to "🇳🇱",
    "Japon" to "🇯🇵",
    "Chine" to "🇨🇳",
    "Corée du Sud" to "🇰🇷",
    "Brésil" to "🇧🇷",
    "Mexique" to "🇲🇽",
    "Turquie" to "🇹🇷"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    navController: NavController,
    viewModel: ProductFormViewModel
) {
    val formData = viewModel.formData
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showExitDialog by remember { mutableStateOf(false) }

    val countryFlags = remember { getCountryFlags() }
    val selectedFlag = countryFlags[formData.country] ?: "🌍"

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.updatePurchaseDate(dateFormatter.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmer", color = Primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Primary,
                    todayContentColor = Primary,
                    todayDateBorderColor = Primary
                )
            )
        }
    }

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
                            "Nouveau produit",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (formData.productName.isNotBlank() ||
                                    formData.country.isNotBlank() ||
                                    formData.customImageUri != null
                                ) {
                                    showExitDialog = true
                                } else {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ArrowBack, "Retour", tint = Primary)
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
                // === STEP HEADER ===
                StepHeader(
                    stepNumber = 1,
                    totalSteps = 3,
                    title = "Informations de base",
                    subtitle = "Type et détails principaux"
                )

                // === TYPE DE PRODUIT (SECTION SUPPRIMÉE) ===
                // REMOVED - Plus de section Type de produit

                // === INFORMATIONS PRODUIT ===
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
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Primary.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Info, null, tint = Primary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Informations produit",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1C1B1F)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // Nom du produit
                        OutlinedTextField(
                            value = formData.productName,
                            onValueChange = viewModel::updateProductName,
                            label = { Text("Nom du produit *") },
                            leadingIcon = { Icon(Icons.Default.ShoppingCart, null, tint = Primary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary,
                                unfocusedBorderColor = Color.LightGray.copy(0.5f),
                                cursorColor = Primary,
                                focusedContainerColor = Primary.copy(0.03f)
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        // Date d'achat
                        OutlinedTextField(
                            value = formData.purchaseDate,
                            onValueChange = {},
                            label = { Text("Date d'achat *") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = Primary) },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, null, tint = Primary)
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                            enabled = false,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Primary.copy(0.5f),
                                disabledLabelColor = Primary.copy(0.7f),
                                disabledLeadingIconColor = Primary.copy(0.7f),
                                disabledTrailingIconColor = Primary,
                                disabledTextColor = Color(0xFF1C1B1F)
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        // === COUNTRY PICKER WITH FLAGS ===
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCountryPicker = !showCountryPicker },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (formData.country.isNotBlank())
                                    Primary.copy(0.05f)
                                else
                                    Color.White
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (formData.country.isNotBlank()) Primary else Color.LightGray.copy(0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Primary.copy(0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(selectedFlag, fontSize = 20.sp)
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Pays d'origine",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (formData.country.isNotBlank()) Primary else Color.Gray
                                    )
                                    if (formData.country.isNotBlank()) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            formData.country,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF1C1B1F)
                                        )
                                    } else {
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            "Sélectionnez un pays",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray.copy(0.6f)
                                        )
                                    }
                                }

                                Icon(
                                    if (showCountryPicker) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    null,
                                    tint = Primary
                                )
                            }
                        }

                        // Country Picker Menu
                        AnimatedVisibility(
                            visible = showCountryPicker,
                            enter = slideInVertically() + expandVertically() + fadeIn(),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(0.05f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp)
                                        .verticalScroll(rememberScrollState())
                                        .padding(vertical = 8.dp)
                                ) {
                                    countryFlags.forEach { (country, flag) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.updateCountry(country)
                                                    showCountryPicker = false
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                }
                                                .background(
                                                    if (country == formData.country) Primary.copy(0.1f) else Color.Transparent
                                                )
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(flag, fontSize = 24.sp, modifier = Modifier.size(32.dp))
                                            Spacer(Modifier.width(16.dp))
                                            Text(
                                                country,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = if (country == formData.country) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (country == formData.country) Primary else Color(0xFF1C1B1F)
                                            )
                                            if (country == formData.country) {
                                                Spacer(Modifier.weight(1f))
                                                Icon(Icons.Default.Check, null, tint = Primary, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // === BOUTON SUIVANT ===
                Button(
                    onClick = {
                        if (viewModel.isStep1Valid()) {
                            navController.navigate(Screen.Step2.route)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = viewModel.isStep1Valid()
                ) {
                    Box(
                        Modifier.fillMaxSize().background(
                            if (viewModel.isStep1Valid())
                                Brush.horizontalGradient(listOf(Primary, PrimaryContainer))
                            else
                                Brush.horizontalGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f))),
                            RoundedCornerShape(20.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Suivant", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp), color = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Box(Modifier.size(32.dp).background(Color.White.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
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
                        Box(Modifier.size(64.dp).background(MaterialTheme.colorScheme.error.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
                        }
                    },
                    title = { Text("Quitter le formulaire ?", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("Vos modifications seront perdues.", color = Color.Gray)
                            if (formData.customImageUri != null) {
                                Spacer(Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.3f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Photo, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Image personnalisée perdue", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    },
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
