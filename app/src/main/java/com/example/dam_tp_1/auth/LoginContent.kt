package com.example.dam_tp_1.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginContent(
    viewModel: AuthViewModel,
    productViewModel: ProductFormViewModel, // ✅ Ajouté
    onLogin: () -> Unit,
    onSwitchToSignup: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
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

            Spacer(Modifier.height(32.dp))

            // === LOGO ===
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
                            .size(100.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                Brush.verticalGradient(listOf(Primary, PrimaryContainer)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "MyProd",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 42.sp
                        ),
                        color = Primary
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Gérez vos produits intelligemment",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // === TITRE ===
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 200))
            ) {
                Column {
                    Text(
                        text = "Bon retour ! 👋",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1C1B1F)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Connectez-vous pour continuer",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // === EMAIL ===
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 300))
            ) {
                Column {
                    Text(
                        "Email",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1C1B1F),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("votre.email@exemple.com", color = Color.Gray.copy(0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.LightGray.copy(0.5f),
                            cursorColor = Primary,
                            focusedContainerColor = Primary.copy(0.03f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // === PASSWORD ===
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 400))
            ) {
                Column {
                    Text(
                        "Mot de passe",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1C1B1F),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••", color = Color.Gray.copy(0.5f)) },
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
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.LightGray.copy(0.5f),
                            cursorColor = Primary,
                            focusedContainerColor = Primary.copy(0.03f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // === FORGOT PASSWORD ===
            AnimatedVisibility(visible = isVisible, enter = fadeIn(tween(600, delayMillis = 500))) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {}) {
                        Text("Mot de passe oublié ?", color = Primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // === BUTTON LOGIN WITH GRADIENT ===
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 600)) + scaleIn(spring(Spring.DampingRatioMediumBouncy))
            ) {
                Button(
                    onClick = {
                        // ✅ Appeler login avec productViewModel
                        viewModel.login(
                            email = email,
                            password = password,
                            productViewModel = productViewModel,
                            onSuccess = onLogin,
                            onError = { error ->
                                viewModel.setError(error)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                if (email.isNotBlank() && password.isNotBlank() && !isLoading)
                                    Brush.horizontalGradient(listOf(Primary, PrimaryContainer))
                                else
                                    Brush.horizontalGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f))),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(32.dp).background(Color.White.copy(0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Login, null, Modifier.size(20.dp), tint = Color.White)
                                }
                                Spacer(Modifier.width(12.dp))
                                Text("Se connecter", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp), color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // === DIVIDER ===
            AnimatedVisibility(visible = isVisible, enter = fadeIn(tween(600, delayMillis = 700))) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = Color.LightGray.copy(0.3f), thickness = 1.dp)
                    Text(" ou ", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
                    HorizontalDivider(Modifier.weight(1f), color = Color.LightGray.copy(0.3f), thickness = 1.dp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // === SIGNUP LINK ===
            AnimatedVisibility(visible = isVisible, enter = fadeIn(tween(600, delayMillis = 800))) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Pas encore de compte ?", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = onSwitchToSignup) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("S'inscrire", color = Primary, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, null, Modifier.size(16.dp), tint = Primary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun AuthViewModel.login(
    email: String,
    password: String,
    productViewModel: ProductFormViewModel,
    onSuccess: () -> Unit,
    onError: Any
) {
}
