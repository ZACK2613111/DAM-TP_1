package com.example.dam_tp_1.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dam_tp_1.ui.theme.OnSurface
import com.example.dam_tp_1.ui.theme.Primary
import com.example.dam_tp_1.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupContent(
    viewModel: AuthViewModel,
    onSignup: () -> Unit,
    onSwitchToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var pays by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCountryDropdown by remember { mutableStateOf(false) }

    // ✅ AJOUT DE LA CHECKBOX POUR LES CONDITIONS GÉNÉRALES
    var acceptTerms by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    // ✅ CONFIGURATION DE LA DATE PICKER AVEC RESTRICTIONS
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val minYear = currentYear - 100 // Maximum 100 ans
    val maxYear = currentYear - 13   // Minimum 13 ans

    val datePickerState = rememberDatePickerState(
        // Limiter la sélection de date
        yearRange = minYear..maxYear,
        initialSelectedDateMillis = Calendar.getInstance().apply {
            set(maxYear, 0, 1) // 1er janvier de l'année max (il y a 13 ans)
        }.timeInMillis
    )

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    // ✅ AMÉLIORATION DU CALCUL D'ÂGE AVEC VALIDATION STRICTE
    val ageValidation = remember(birthDate) {
        if (birthDate.isNotBlank()) {
            try {
                val birthDateParsed = dateFormatter.parse(birthDate)
                val today = Calendar.getInstance()
                val birthCalendar = Calendar.getInstance()
                birthCalendar.time = birthDateParsed

                // Vérifier que la date n'est pas dans le futur
                if (birthCalendar.after(today)) {
                    return@remember AgeValidation(0, "La date de naissance ne peut pas être dans le futur")
                }

                var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

                // Ajustement précis de l'âge
                if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }

                when {
                    age < 13 -> AgeValidation(age, "Vous devez avoir au moins 13 ans pour créer un compte")
                    age > 120 -> AgeValidation(age, "Veuillez vérifier votre date de naissance")
                    else -> AgeValidation(age, null)
                }
            } catch (e: Exception) {
                AgeValidation(0, "Format de date invalide")
            }
        } else {
            AgeValidation(0, null)
        }
    }

    val passwordsMatch = password == confirmPassword

    // ✅ VALIDATION COMPLÈTE AVEC CONDITIONS GÉNÉRALES
    val isFormValid = nom.isNotBlank() &&
            prenom.isNotBlank() &&
            ageValidation.age >= 13 &&
            ageValidation.errorMessage == null &&
            pays.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            passwordsMatch &&
            birthDate.isNotBlank() &&
            acceptTerms // ✅ Conditions générales obligatoires

    // Liste des pays populaires
    val countries = listOf(
        "🇩🇿 Algérie", "🇫🇷 France", "🇲🇦 Maroc", "🇹🇳 Tunisie",
        "🇨🇦 Canada", "🇺🇸 États-Unis", "🇬🇧 Royaume-Uni",
        "🇩🇪 Allemagne", "🇮🇹 Italie", "🇪🇸 Espagne", "🇳🇱 Pays-Bas",
        "🇧🇪 Belgique", "🇨🇭 Suisse", "🇸🇪 Suède", "🇳🇴 Norvège"
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        birthDate = dateFormatter.format(Date(millis))
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmer", color = Primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler", color = OnSurface.copy(alpha = 0.6f))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Primary,
                    todayContentColor = Primary,
                    todayDateBorderColor = Primary,
                    headlineContentColor = Primary
                ),
                title = {
                    Text(
                        "Sélectionnez votre date de naissance",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                headline = {
                    Text(
                        "Date de naissance",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(16.dp))

        // Header avec bouton retour
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onBack()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
            Text(
                text = "Inscription",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(Modifier.size(40.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Carte principale
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icône
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Créons votre compte !",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Remplissez vos informations pour commencer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Section Informations personnelles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Informations personnelles",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                    Spacer(Modifier.width(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Primary.copy(alpha = 0.3f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Prénom et Nom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = prenom,
                        onValueChange = { prenom = it.trimStart() },
                        label = { Text("Prénom") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Primary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedLeadingIconColor = Primary
                        )
                    )
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it.trimStart() },
                        label = { Text("Nom") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Primary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedLeadingIconColor = Primary
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ✅ DATE DE NAISSANCE AVEC VALIDATION AMÉLIORÉE
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },
                    label = { Text("Date de naissance") },
                    leadingIcon = { Icon(Icons.Default.Cake, null, tint = Primary) },
                    trailingIcon = {
                        Row {
                            if (ageValidation.age > 0 && ageValidation.errorMessage == null) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${ageValidation.age} ans",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            IconButton(onClick = {
                                showDatePicker = true
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }) {
                                Icon(Icons.Default.DateRange, null, tint = Primary)
                            }
                        }
                    },
                    placeholder = { Text("Sélectionnez votre date de naissance") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDatePicker = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (ageValidation.errorMessage == null) Primary else MaterialTheme.colorScheme.error,
                        focusedLabelColor = if (ageValidation.errorMessage == null) Primary else MaterialTheme.colorScheme.error,
                        unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                        focusedLeadingIconColor = Primary
                    ),
                    supportingText = {
                        ageValidation.errorMessage?.let { error ->
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } ?: run {
                            if (ageValidation.age > 0) {
                                Text(
                                    "✓ Âge valide pour créer un compte",
                                    color = Color(0xFF4CAF50),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    },
                    isError = ageValidation.errorMessage != null
                )

                Spacer(Modifier.height(16.dp))

                // Pays avec dropdown
                ExposedDropdownMenuBox(
                    expanded = showCountryDropdown,
                    onExpandedChange = {
                        showCountryDropdown = it
                        if (it) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                ) {
                    OutlinedTextField(
                        value = pays,
                        onValueChange = { },
                        label = { Text("Pays de résidence") },
                        leadingIcon = { Icon(Icons.Default.Public, null, tint = Primary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCountryDropdown) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedLeadingIconColor = Primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showCountryDropdown,
                        onDismissRequest = { showCountryDropdown = false }
                    ) {
                        countries.forEach { country ->
                            DropdownMenuItem(
                                text = { Text(country) },
                                onClick = {
                                    pays = country
                                    showCountryDropdown = false
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Section Compte
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Informations du compte",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                    Spacer(Modifier.width(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Primary.copy(alpha = 0.3f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text("Adresse email") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Primary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                        focusedLeadingIconColor = Primary
                    ),
                    supportingText = {
                        Text(
                            "Un email de confirmation sera envoyé à cette adresse",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface.copy(alpha = 0.6f)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Mot de passe avec indicateur de force amélioré
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary) },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                tint = Primary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                        focusedLeadingIconColor = Primary
                    ),
                    supportingText = {
                        if (password.isNotEmpty()) {
                            val requirements = listOf(
                                "8 caractères min" to (password.length >= 8),
                                "1 majuscule" to password.any { it.isUpperCase() },
                                "1 chiffre" to password.any { it.isDigit() },
                                "1 caractère spécial" to password.any { "!@#\$%^&*()_+=-".contains(it) }
                            )

                            val metCount = requirements.count { it.second }
                            val strength = when (metCount) {
                                0, 1 -> "Faible" to MaterialTheme.colorScheme.error
                                2 -> "Moyen" to Color(0xFFFF9800)
                                3 -> "Bon" to Color(0xFF2196F3)
                                4 -> "Excellent" to Color(0xFF4CAF50)
                                else -> "Faible" to MaterialTheme.colorScheme.error
                            }

                            Column {
                                // Indicateur de force
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        "Force: ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        strength.first,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = strength.second
                                    )
                                }

                                // Critères
                                requirements.forEach { (requirement, isMet) ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = if (isMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            requirement,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Confirmation mot de passe
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmer le mot de passe") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            null,
                            tint = if (passwordsMatch || confirmPassword.isEmpty()) Primary else MaterialTheme.colorScheme.error
                        )
                    },
                    trailingIcon = {
                        Row {
                            if (confirmPassword.isNotEmpty()) {
                                Icon(
                                    imageVector = if (passwordsMatch) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (passwordsMatch) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null,
                                    tint = Primary
                                )
                            }
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    isError = confirmPassword.isNotBlank() && !passwordsMatch,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (passwordsMatch || confirmPassword.isEmpty()) Primary else MaterialTheme.colorScheme.error,
                        focusedLabelColor = if (passwordsMatch || confirmPassword.isEmpty()) Primary else MaterialTheme.colorScheme.error,
                        unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = if (passwordsMatch || confirmPassword.isEmpty()) Primary else MaterialTheme.colorScheme.error
                    ),
                    supportingText = {
                        if (confirmPassword.isNotBlank() && !passwordsMatch) {
                            Text(
                                "Les mots de passe ne correspondent pas",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (confirmPassword.isNotBlank() && passwordsMatch) {
                            Text(
                                "✓ Les mots de passe correspondent",
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                // ✅ CHECKBOX POUR LES CONDITIONS GÉNÉRALES
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                acceptTerms = !acceptTerms
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = acceptTerms,
                            onCheckedChange = {
                                acceptTerms = it
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Primary,
                                uncheckedColor = OnSurface.copy(alpha = 0.6f)
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "J'accepte les conditions générales",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = OnSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Row {
                                TextButton(
                                    onClick = {
                                        // TODO: Ouvrir les conditions générales
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Primary
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Conditions d'utilisation",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    " et ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface.copy(alpha = 0.7f)
                                )
                                TextButton(
                                    onClick = {
                                        // TODO: Ouvrir la politique de confidentialité
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Primary
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Politique de confidentialité",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Bouton d'inscription avec validation complète
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.registerUser(
                                nom = nom,
                                prenom = prenom,
                                age = ageValidation.age, // ✅ Utiliser l'âge validé
                                pays = pays,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                onSuccess = onSignup
                            )
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = OnSurface.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.PersonAdd,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Créer mon compte",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // ✅ INDICATEUR DE VALIDATION MANQUANTE
                if (!acceptTerms && nom.isNotBlank() && prenom.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Veuillez accepter les conditions générales pour continuer",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                // Message d'erreur
                errorMessage?.let {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = OnSurface.copy(alpha = 0.12f)
                    )
                    Text(
                        " ou ",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = OnSurface.copy(alpha = 0.12f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Lien vers connexion
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Déjà un compte ? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface.copy(alpha = 0.7f)
                    )
                    TextButton(
                        onClick = {
                            onSwitchToLogin()
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                    ) {
                        Text(
                            "Se connecter",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ✅ DATA CLASS POUR LA VALIDATION D'ÂGE
data class AgeValidation(
    val age: Int,
    val errorMessage: String?
)
