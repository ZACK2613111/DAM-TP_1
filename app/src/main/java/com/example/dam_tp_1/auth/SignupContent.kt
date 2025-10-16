package com.example.dam_tp_1.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
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
    val scope = rememberCoroutineScope()

    var isVisible by remember { mutableStateOf(false) }
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
    var acceptTerms by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    // Date picker configuration
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val minYear = currentYear - 100
    val maxYear = currentYear - 13

    val datePickerState = rememberDatePickerState(
        yearRange = minYear..maxYear,
        initialSelectedDateMillis = Calendar.getInstance().apply {
            set(maxYear, 0, 1)
        }.timeInMillis
    )

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    // Age validation
    val ageValidation = remember(birthDate) {
        if (birthDate.isNotBlank()) {
            try {
                val birthDateParsed = dateFormatter.parse(birthDate)
                val today = Calendar.getInstance()
                val birthCalendar = Calendar.getInstance()
                birthCalendar.time = birthDateParsed

                if (birthCalendar.after(today)) {
                    return@remember AgeValidation(0, "La date ne peut pas être dans le futur")
                }

                var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }

                when {
                    age < 13 -> AgeValidation(age, "Vous devez avoir au moins 13 ans")
                    age > 120 -> AgeValidation(age, "Veuillez vérifier votre date")
                    else -> AgeValidation(age, null)
                }
            } catch (e: Exception) {
                AgeValidation(0, "Format invalide")
            }
        } else {
            AgeValidation(0, null)
        }
    }

    val passwordsMatch = password == confirmPassword
    val isFormValid = nom.isNotBlank() && prenom.isNotBlank() &&
            ageValidation.age >= 13 && ageValidation.errorMessage == null &&
            pays.isNotBlank() && email.isNotBlank() &&
            password.isNotBlank() && passwordsMatch &&
            birthDate.isNotBlank() && acceptTerms

    val countries = listOf(
        "🇩🇿 Algérie", "🇫🇷 France", "🇲🇦 Maroc", "🇹🇳 Tunisie",
        "🇨🇦 Canada", "🇺🇸 États-Unis", "🇬🇧 Royaume-Uni",
        "🇩🇪 Allemagne", "🇮🇹 Italie", "🇪🇸 Espagne"
    )

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Date Picker Dialog
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
                    Text("Annuler", color = Color.Gray)
                }
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // === HEADER ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(24.dp))

            // === LOGO ET TITRE ===
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { -it / 2 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Primary, PrimaryContainer)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "MyProd",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp
                        ),
                        color = Primary
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Créons votre compte !",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1C1B1F)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Remplissez vos informations pour commencer",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // === FORMULAIRE (même structure mais avec animations) ===
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 200))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Nom et Prénom en ligne
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModernTextField(
                            value = prenom,
                            onValueChange = { prenom = it.trimStart() },
                            label = "Prénom",
                            leadingIcon = Icons.Default.Person,
                            modifier = Modifier.weight(1f)
                        )

                        ModernTextField(
                            value = nom,
                            onValueChange = { nom = it.trimStart() },
                            label = "Nom",
                            leadingIcon = Icons.Default.Person,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Date de naissance
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = {},
                        label = { Text("Date de naissance") },
                        leadingIcon = { Icon(Icons.Default.Cake, null, tint = Primary) },
                        trailingIcon = {
                            Row {
                                if (ageValidation.age > 0 && ageValidation.errorMessage == null) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "${ageValidation.age} ans",
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
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            cursorColor = Primary,
                            focusedContainerColor = Primary.copy(alpha = 0.03f)
                        ),
                        supportingText = {
                            ageValidation.errorMessage?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        isError = ageValidation.errorMessage != null
                    )

                    // Pays
                    ExposedDropdownMenuBox(
                        expanded = showCountryDropdown,
                        onExpandedChange = { showCountryDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = pays,
                            onValueChange = {},
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
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                cursorColor = Primary,
                                focusedContainerColor = Primary.copy(alpha = 0.03f)
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

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Informations du compte",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary
                    )

                    // Email
                    ModernTextField(
                        value = email,
                        onValueChange = { email = it.trim() },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        supportingText = "Un email de confirmation sera envoyé"
                    )

                    // Mot de passe
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            cursorColor = Primary,
                            focusedContainerColor = Primary.copy(alpha = 0.03f)
                        )
                    )

                    // Confirmer mot de passe
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
                                        if (passwordsMatch) Icons.Default.Check else Icons.Default.Close,
                                        null,
                                        tint = if (passwordsMatch) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
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
                        shape = RoundedCornerShape(16.dp),
                        isError = confirmPassword.isNotBlank() && !passwordsMatch,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (passwordsMatch || confirmPassword.isEmpty()) Primary else MaterialTheme.colorScheme.error,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            cursorColor = Primary,
                            focusedContainerColor = Primary.copy(alpha = 0.03f)
                        ),
                        supportingText = {
                            if (confirmPassword.isNotBlank() && !passwordsMatch) {
                                Text("Les mots de passe ne correspondent pas", color = MaterialTheme.colorScheme.error)
                            } else if (confirmPassword.isNotBlank() && passwordsMatch) {
                                Text("✓ Les mots de passe correspondent", color = Color(0xFF4CAF50))
                            }
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Checkbox conditions
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Primary.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.1f))
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
                                colors = CheckboxDefaults.colors(checkedColor = Primary)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "J'accepte les conditions générales et la politique de confidentialité",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1C1B1F)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Spacer(Modifier.height(16.dp))  // ✅ Plus d'espace

// === BOUTON INSCRIPTION MODERNE ===
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.registerUser(
                                    nom = nom,
                                    prenom = prenom,
                                    age = ageValidation.age,
                                    pays = pays,
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    onSuccess = onSignup
                                )
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),  // Plus grand
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(20.dp),
                        enabled = isFormValid && !isLoading,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = if (isFormValid && !isLoading) {
                                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            colors = listOf(Primary, PrimaryContainer)
                                        )
                                    } else {
                                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Gray.copy(alpha = 0.3f),
                                                Color.Gray.copy(alpha = 0.3f)
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Color.White,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PersonAdd,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Créer mon compte",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Lien vers connexion
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Déjà un compte ?", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        TextButton(onClick = onSwitchToLogin) {
                            Text("Se connecter", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, null, tint = Primary) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            cursorColor = Primary,
            focusedContainerColor = Primary.copy(alpha = 0.03f),
            unfocusedContainerColor = Color.Gray.copy(alpha = 0.02f)
        ),
        supportingText = supportingText?.let { { Text(it, color = Color.Gray, style = MaterialTheme.typography.bodySmall) } }
    )
}

data class AgeValidation(
    val age: Int,
    val errorMessage: String?
)
