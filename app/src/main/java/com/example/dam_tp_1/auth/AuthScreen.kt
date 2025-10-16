package com.example.dam_tp_1.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(AuthTab.Welcome) }
    val haptic = LocalHapticFeedback.current

    // Observer les états du ViewModel
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val isEmailVerified by authViewModel.isEmailVerified.collectAsStateWithLifecycle()

    // Navigation automatique si l'utilisateur est connecté ET vérifié
    LaunchedEffect(currentUser, isEmailVerified) {
        if (currentUser != null && isEmailVerified) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    // Afficher les messages d'erreur
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            delay(6000)
            authViewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientStart.copy(alpha = 0.8f),
                        GradientEnd.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        when (currentTab) {
            AuthTab.Welcome -> WelcomeContent(
                onGetStarted = {
                    currentTab = AuthTab.Login
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            )
            AuthTab.Login -> LoginContent(
                viewModel = authViewModel,
                onLogin = {
                    // Si l'utilisateur n'est pas vérifié, passer à l'écran de vérification
                    if (currentUser != null && !isEmailVerified) {
                        currentTab = AuthTab.EmailVerification
                    }
                },
                onSwitchToSignup = {
                    currentTab = AuthTab.Signup
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onBack = {
                    currentTab = AuthTab.Welcome
                    authViewModel.resetAuthState()
                }
            )
            AuthTab.Signup -> SignupContent(
                viewModel = authViewModel,
                onSignup = {
                    // Après inscription, passer à l'écran de vérification
                    currentTab = AuthTab.EmailVerification
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onSwitchToLogin = {
                    currentTab = AuthTab.Login
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onBack = {
                    currentTab = AuthTab.Welcome
                    authViewModel.resetAuthState()
                }
            )
            // ✅ NOUVEL ÉCRAN DE VÉRIFICATION EMAIL
            AuthTab.EmailVerification -> EmailVerificationContent(
                viewModel = authViewModel,
                onVerificationComplete = {
                    // Navigation gérée par LaunchedEffect ci-dessus
                },
                onBack = {
                    currentTab = AuthTab.Login
                    authViewModel.resetAuthState()
                },
                onSwitchToSignup = {
                    currentTab = AuthTab.Signup
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            )
        }

        // Affichage des erreurs - Amélioré
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { authViewModel.clearError() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Indicateur de chargement - Amélioré
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Chargement...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

enum class AuthTab {
    Welcome, Login, Signup, EmailVerification // ✅ Ajout du nouvel état
}