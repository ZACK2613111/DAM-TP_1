package com.example.dam_tp_1.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dam_tp_1.api.CountriesApiService
import com.example.dam_tp_1.components.StepHeader
import com.example.dam_tp_1.components.ProductImagePicker
import com.example.dam_tp_1.data.Country
import com.example.dam_tp_1.model.ProductType
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showExitDialog by remember { mutableStateOf(false) }

    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var isLoadingCountries by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoadingCountries = true
        try {
            val api = CountriesApiService.create()
            countries = api.getAllCountries().sortedBy { it.name.common }
        } catch (e: Exception) {
            // Fallback silently
        } finally {
            isLoadingCountries = false
        }
    }

    val filteredCountries = remember(countries, searchQuery) {
        if (searchQuery.isEmpty()) countries
        else countries.filter { it.name.common.contains(searchQuery, ignoreCase = true) }
    }

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

    Box(Modifier.fillMaxSize().background(Color.White)) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Nouveau produit", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF1C1B1F))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (formData.productName.isNotBlank() || formData.country.isNotBlank() || formData.customImageUri != null) {
                                showExitDialog = true
                            } else {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        }) {
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
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StepHeader(stepNumber = 1, totalSteps = 3, title = "Informations de base", subtitle = "Type et détails principaux")

                // === IMAGE PICKER ===
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Gray.copy(0.1f))) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(Modifier.size(40.dp).background(Primary.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Image, null, tint = Primary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Image du produit", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                        }

                        Spacer(Modifier.height(20.dp))

                        ProductImagePicker(
                            selectedType = formData.selectedType,
                            customImageUri = formData.customImageUri,
                            onImageSelected = { uri ->
                                viewModel.updateCustomImage(uri as Uri?)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        )

                        Spacer(Modifier.height(12.dp))
                        Text("Optionnel - Personnalisez l'image", style = MaterialTheme.typography.bodySmall, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }

                // === TYPE DE PRODUIT ===
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Gray.copy(0.1f))) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(Modifier.size(40.dp).background(Primary.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Category, null, tint = Primary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Type de produit", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProductType.entries.forEach { type ->
                                Card(
                                    modifier = Modifier.weight(1f).clickable {
                                        viewModel.updateFormData { it.copy(selectedType = type) }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (type == formData.selectedType) Primary.copy(0.15f) else Color.White),
                                    border = BorderStroke(2.dp, if (type == formData.selectedType) Primary else Color.Gray.copy(0.2f))
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                        Box(Modifier.size(48.dp).background(if (type == formData.selectedType) Primary.copy(0.2f) else Color.Gray.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                            Icon(
                                                when(type) {
                                                    ProductType.Consumable -> Icons.Default.ShoppingCart
                                                    ProductType.Durable -> Icons.Default.Watch
                                                    ProductType.Other -> Icons.Default.MoreHoriz
                                                },
                                                null,
                                                tint = if (type == formData.selectedType) Primary else Color.Gray,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(type.displayName, style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (type == formData.selectedType) FontWeight.Bold else FontWeight.Medium), color = if (type == formData.selectedType) Primary else Color.Gray, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }

                // === INFORMATIONS PRODUIT ===
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Gray.copy(0.1f))) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(Modifier.size(40.dp).background(Primary.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Info, null, tint = Primary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Informations produit", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                        }

                        Spacer(Modifier.height(20.dp))

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

                        // === COUNTRY PICKER ===
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { showCountryPicker = !showCountryPicker },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = if (formData.country.isNotBlank()) Primary.copy(0.05f) else Color.White),
                            border = BorderStroke(1.5.dp, if (formData.country.isNotBlank()) Primary else Color.LightGray.copy(0.5f))
                        ) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                val selectedCountry = countries.find { it.name.common == formData.country }

                                if (selectedCountry != null) {
                                    AsyncImage(model = selectedCountry.flags.png, contentDescription = null, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)))
                                } else {
                                    Box(Modifier.size(40.dp).background(Primary.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Language, null, tint = Primary, modifier = Modifier.size(24.dp))
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Pays d'origine", style = MaterialTheme.typography.labelMedium, color = if (formData.country.isNotBlank()) Primary else Color.Gray)
                                    if (formData.country.isNotBlank()) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(formData.country, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1C1B1F))
                                    } else {
                                        Spacer(Modifier.height(2.dp))
                                        Text("Sélectionnez un pays", style = MaterialTheme.typography.bodyMedium, color = Color.Gray.copy(0.6f))
                                    }
                                }

                                Icon(if (showCountryPicker) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Primary)
                            }
                        }

                        AnimatedVisibility(visible = showCountryPicker, enter = slideInVertically() + expandVertically() + fadeIn(), exit = slideOutVertically() + shrinkVertically() + fadeOut()) {
                            Card(Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Gray.copy(0.1f))) {
                                Column(Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = { Text("Rechercher un pays...") },
                                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Primary) },
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary)
                                    )

                                    if (isLoadingCountries) {
                                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = Primary)
                                        }
                                    } else {
                                        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                                            items(filteredCountries) { country ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().clickable {
                                                        viewModel.updateCountry(country.name.common)
                                                        showCountryPicker = false
                                                        searchQuery = ""
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    }.background(if (country.name.common == formData.country) Primary.copy(0.1f) else Color.Transparent).padding(horizontal = 16.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    AsyncImage(model = country.flags.png, contentDescription = null, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)))
                                                    Spacer(Modifier.width(16.dp))
                                                    Text(country.name.common, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (country.name.common == formData.country) FontWeight.Bold else FontWeight.Normal), color = if (country.name.common == formData.country) Primary else Color(0xFF1C1B1F), modifier = Modifier.weight(1f))
                                                    if (country.name.common == formData.country) {
                                                        Icon(Icons.Default.Check, null, tint = Primary, modifier = Modifier.size(20.dp))
                                                    }
                                                }
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
                            if (viewModel.isStep1Valid()) Brush.horizontalGradient(listOf(Primary, PrimaryContainer))
                            else Brush.horizontalGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f))),
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
                    text = { Text("Vos modifications seront perdues.", color = Color.Gray) },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                            viewModel.resetForm()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
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
