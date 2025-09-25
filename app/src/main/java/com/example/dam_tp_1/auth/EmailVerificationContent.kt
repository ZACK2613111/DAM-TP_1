package com.example.dam_tp_1.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationContent(
    viewModel: AuthViewModel,
    onVerificationComplete: () -> Unit,
    onBack: () -> Unit,
    onSwitchToSignup: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isEmailVerified by viewModel.isEmailVerified.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var resendTimer by remember { mutableStateOf(0) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    // Timer pour renvoyer l'email
    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) {
            delay(1000)
            resendTimer -= 1
        }
    }

    // Animation d'entrée
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    // Vérifier automatiquement l'état de vérification
    LaunchedEffect(Unit) {
        while (!isEmailVerified && currentUser != null) {
            delay(3000) // Vérifier toutes les 3 secondes
            viewModel.checkEmailVerification()
        }
    }

    // Navigation quand vérifié
    LaunchedEffect(isEmailVerified) {
        if (isEmailVerified) {
            showSuccessMessage = true
            delay(2000)
            onVerificationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header avec bouton retour
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
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
                text = "Vérification",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(Modifier.size(40.dp))
        }

        Spacer(Modifier.height(32.dp))

        // Carte principale
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icône animée
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    // Cercle de fond avec animation
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (showSuccessMessage) {
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            } else {
                                Primary.copy(alpha = 0.1f)
                            }
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AnimatedContent(
                                targetState = if (showSuccessMessage) "success" else "email",
                                transitionSpec = {
                                    (slideInVertically() + fadeIn()).togetherWith(slideOutVertically() + fadeOut())
                                },
                                label = "icon"
                            ) { state ->
                                when (state) {
                                    "success" -> Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    else -> Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Cercles d'animation
                    if (!showSuccessMessage) {
                        repeat(3) { index ->
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 0.7f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(2000, delayMillis = index * 400),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "alpha"
                            )
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(2000, delayMillis = index * 400),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )

                            Box(
                                modifier = Modifier
                                    .size((100 + index * 10).dp)
                                    .scale(scale)
                                    .background(
                                        Primary.copy(alpha = alpha * 0.3f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Titre et description
                AnimatedContent(
                    targetState = showSuccessMessage,
                    transitionSpec = {
                        slideInVertically() + fadeIn() togetherWith
                                slideOutVertically() + fadeOut()
                    },
                    label = "content"
                ) { isSuccess ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isSuccess) {
                            Text(
                                text = "Email vérifié !",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF4CAF50)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Parfait ! Votre compte est maintenant activé.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "Vérifiez votre email",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Un email de confirmation a été envoyé à :",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Primary.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = currentUser?.email ?: "email@example.com",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Primary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (!showSuccessMessage) {
                    Spacer(Modifier.height(32.dp))

                    // Instructions
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Instructions",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Primary
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "• Ouvrez votre application email\n• Cherchez un email de MyCollection\n• Cliquez sur le lien de confirmation\n• Revenez ici, la vérification sera automatique",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Bouton de renvoi
                    Button(
                        onClick = {
                            viewModel.resendVerificationEmail {
                                resendTimer = 60
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (resendTimer > 0) OnSurface.copy(alpha = 0.12f) else Primary,
                            contentColor = if (resendTimer > 0) OnSurface.copy(alpha = 0.38f) else Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = resendTimer == 0 && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else if (resendTimer > 0) {
                            Text(
                                "Renvoyer dans ${resendTimer}s",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Renvoyer l'email",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Bouton de vérification manuelle
                    OutlinedButton(
                        onClick = {
                            viewModel.refreshEmailVerificationStatus()
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "J'ai vérifié mon email",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Email incorrect ?
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Email incorrect ?",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Si vous avez utilisé la mauvaise adresse email, créez un nouveau compte avec la bonne adresse.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            TextButton(
                                onClick = onSwitchToSignup,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(
                                    "Créer un nouveau compte",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
