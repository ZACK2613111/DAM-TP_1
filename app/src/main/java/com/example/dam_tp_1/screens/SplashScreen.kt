package com.example.dam_tp_1.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val haptic = LocalHapticFeedback.current
    var isVisible by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentStep by remember { mutableStateOf("Initialisation...") }

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2000, easing = EaseInOutCubic),
        label = "progress"
    )

    // Launch effects - MODIFIED to go directly to Auth
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        delay(800)
        showProgressBar = true

        // Simulation des étapes de chargement
        val steps = listOf(
            "Connexion à Firebase..." to 0.2f,
            "Préparation de l'interface..." to 0.5f,
            "Chargement des composants..." to 0.8f,
            "Finalisation..." to 1.0f
        )

        for ((step, targetProgress) in steps) {
            currentStep = step

            // Animation progressive
            while (progress < targetProgress) {
                progress += 0.02f
                delay(50)
            }
            delay(300)
        }

        delay(500)

        // ✅ NAVIGATION DIRECTE VERS AUTH (pas de vérification d'utilisateur)
        println("🔍 DEBUG: Navigation directe vers Auth")
        navController.navigate(Screen.Auth.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo animé
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scale),
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Nom de l'app
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally() + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "MyCollection",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier.graphicsLayer { this.alpha = alpha }
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Gérez vos produits avec style",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.graphicsLayer { this.alpha = alpha }
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Progress bar
            AnimatedVisibility(
                visible = showProgressBar,
                enter = slideInVertically() + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.width(200.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = currentStep,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Version en bas
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Version 1.0.0 - Made with ❤️",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}
