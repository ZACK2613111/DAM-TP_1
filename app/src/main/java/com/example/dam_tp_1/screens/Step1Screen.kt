package com.example.dam_tp_1.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    navController: NavController,
    viewModel: ProductFormViewModel
) {
    val formData = viewModel.formData
    val haptic = LocalHapticFeedback.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val context = LocalContext.current

    // === STATES ===
    var showDatePicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    // Countries API
    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var isLoadingCountries by remember { mutableStateOf(false) }

    // === PERMISSIONS - CAMERA + GALLERY ===
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    var permissionsGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.all { it.value }
        if (permissionsGranted) {
            println("✅ Toutes les permissions accordées")
        } else {
            println("❌ Permissions refusées: ${results.filter { !it.value }}")
        }
    }

    // Demander les permissions au démarrage
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions)
    }

    // === LOAD COUNTRIES ===
    LaunchedEffect(Unit) {
        isLoadingCountries = true
        try {
            val api = CountriesApiService.create()
            countries = api.getAllCountries().sortedBy { it.name.common }
        } catch (e: Exception) {
            println("❌ Erreur chargement pays: ${e.message}")
        } finally {
            isLoadingCountries = false
        }
    }

    val filteredCountries = remember(countries, searchQuery) {
        if (searchQuery.isEmpty()) countries
        else countries.filter { it.name.common.contains(searchQuery, ignoreCase = true) }
    }

    // === DATE PICKER DIALOG ===
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.updatePurchaseDate(dateFormatter.format(Date(millis)))
                    }
                    showDatePicker = false
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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

    // === EXIT DIALOG ===
    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = {
                showExitDialog = false
                viewModel.resetForm()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            onDismiss = { showExitDialog = false }
        )
    }

    // === MAIN UI ===
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ModernTopBar(
                title = "Nouveau produit",
                onBackClick = {
                    if (formData.productName.isNotBlank() || formData.country.isNotBlank() || formData.customImageUri != null) {
                        showExitDialog = true
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Step Header
            StepHeader(
                stepNumber = 1,
                totalSteps = 3,
                title = "Informations de base",
                subtitle = "Type et détails principaux"
            )

            // Image Picker Section
            SectionCard(
                icon = Icons.Default.Image,
                title = "Image du produit",
                optional = true
            ) {
                ProductImagePicker(
                    selectedType = formData.selectedType,
                    customImageUri = formData.customImageUri,
                    onImageSelected = { uri ->
                        viewModel.updateCustomImage(
                            if (uri != null) android.net.Uri.parse(uri) else null
                        )
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )

            }

            // Product Type Section - ALIGNEMENT VERTICAL
            SectionCard(
                icon = Icons.Default.Category,
                title = "Type de produit"
            ) {
                ProductTypeSelectorVertical(
                    selectedType = formData.selectedType,
                    onTypeSelected = { type ->
                        viewModel.updateFormData { it.copy(selectedType = type) }
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )
            }

            // Product Information Section
            SectionCard(
                icon = Icons.Default.Info,
                title = "Informations produit"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Product Name
                    ModernTextField(
                        value = formData.productName,
                        onValueChange = viewModel::updateProductName,
                        label = "Nom du produit",
                        icon = Icons.Default.ShoppingCart,
                        required = true
                    )

                    // Purchase Date
                    ModernDateField(
                        value = formData.purchaseDate,
                        label = "Date d'achat",
                        onClick = { showDatePicker = true },
                        required = true
                    )

                    // Country Picker
                    CountryPickerField(
                        selectedCountry = formData.country,
                        countries = countries,
                        filteredCountries = filteredCountries,
                        isExpanded = showCountryPicker,
                        isLoading = isLoadingCountries,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onExpandChange = { showCountryPicker = it },
                        onCountrySelected = { country ->
                            viewModel.updateCountry(country)
                            showCountryPicker = false
                            searchQuery = ""
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }
            }

            // Next Button
            GradientButton(
                text = "Suivant",
                icon = Icons.Default.ArrowForward,
                enabled = viewModel.isStep1Valid(),
                onClick = {
                    navController.navigate(Screen.Step2.route)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// === COMPOSABLES ===

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color(0xFF1C1B1F)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    optional: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray.copy(0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Primary.copy(0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1C1B1F),
                    modifier = Modifier.weight(1f)
                )

                if (optional) {
                    Text(
                        text = "Optionnel",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .background(Color.Gray.copy(0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Content
            content()
        }
    }
}

// ✅ NOUVEAU - ALIGNEMENT VERTICAL DES TYPES
@Composable
private fun ProductTypeSelectorVertical(
    selectedType: ProductType,
    onTypeSelected: (ProductType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductType.entries.forEach { type ->
            ProductTypeCardHorizontal(
                type = type,
                isSelected = type == selectedType,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

// ✅ NOUVELLE CARTE - FORMAT HORIZONTAL (comme une liste)
@Composable
private fun ProductTypeCardHorizontal(
    type: ProductType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(0.15f) else Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Primary else Color.Gray.copy(0.2f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Icône
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (isSelected) Primary.copy(0.2f) else Color.Gray.copy(0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (type) {
                        ProductType.Consumable -> Icons.Default.ShoppingCart
                        ProductType.Durable -> Icons.Default.Watch
                        ProductType.Other -> Icons.Default.MoreHoriz
                    },
                    contentDescription = null,
                    tint = if (isSelected) Primary else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            // Texte
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) Primary else Color(0xFF1C1B1F)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = when(type) {
                        ProductType.Consumable -> "Produits à usage unique"
                        ProductType.Durable -> "Produits réutilisables"
                        ProductType.Other -> "Autres catégories"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Checkmark
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    required: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label + if (required) " *" else "") },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary
            )
        },
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
}

@Composable
private fun ModernDateField(
    value: String,
    label: String,
    onClick: () -> Unit,
    required: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label + if (required) " *" else "") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = Primary
            )
        },
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Primary
                )
            }
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
}

@Composable
private fun CountryPickerField(
    selectedCountry: String,
    countries: List<Country>,
    filteredCountries: List<Country>,
    isExpanded: Boolean,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onExpandChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandChange(!isExpanded) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedCountry.isNotBlank()) Primary.copy(0.05f) else Color.White
            ),
            border = BorderStroke(
                width = 1.5.dp,
                color = if (selectedCountry.isNotBlank()) Primary else Color.LightGray.copy(0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val country = countries.find { it.name.common == selectedCountry }

                if (country != null) {
                    AsyncImage(
                        model = country.flags.png,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Primary.copy(0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pays d'origine",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedCountry.isNotBlank()) Primary else Color.Gray
                    )
                    if (selectedCountry.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = selectedCountry,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                    } else {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Sélectionnez un pays",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray.copy(0.6f)
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Primary
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Gray.copy(0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Rechercher un pays...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            cursorColor = Primary
                        )
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Primary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                        ) {
                            items(filteredCountries) { country ->
                                CountryListItem(
                                    country = country,
                                    isSelected = country.name.common == selectedCountry,
                                    onClick = { onCountrySelected(country.name.common) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryListItem(
    country: Country,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) Primary.copy(0.1f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = country.flags.png,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = country.name.common,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) Primary else Color(0xFF1C1B1F),
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun GradientButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(20.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(listOf(Primary, PrimaryContainer))
                    } else {
                        Brush.horizontalGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f)))
                    },
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.error.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Quitter le formulaire ?",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Vos modifications seront perdues.",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Quitter", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continuer", color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
