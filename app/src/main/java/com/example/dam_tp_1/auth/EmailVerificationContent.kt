package com.example.dam_tp_1.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var resendTimer by remember { mutableIntStateOf(0) }
    var showSuccessMessage by remember { mutableStateOf(false) }
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
        delay(100)
        isVisible = true
    }

    // Vérifier automatiquement l'état de vérification
    LaunchedEffect(Unit) {
        while (!isEmailVerified && currentUser != null) {
            delay(3000)
            viewModel.checkEmailVerification()
        }
    }

    // Navigation quand vérifié
    LaunchedEffect(isEmailVerified) {
        if (isEmailVerified) {
            showSuccessMessage = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(2000)
            onVerificationComplete()
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

            Spacer(Modifier.height(48.dp))

            // === ICÔNE ANIMÉE PRINCIPALE ===
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        // Cercles pulsants
                        if (!showSuccessMessage) {
                            repeat(3) { index ->
                                val infiniteTransition = rememberInfiniteTransition(label = "pulse$index")
                                val alpha by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 0.4f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, delayMillis = index * 400),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "alpha"
                                )
                                val scale by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 1.4f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, delayMillis = index * 400),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "pulseScale"
                                )

                                Box(
                                    modifier = Modifier
                                        .size((100 + index * 15).dp)
                                        .scale(scale)
                                        .background(
                                            Primary.copy(alpha = alpha * 0.3f),
                                            CircleShape
                                        )
                                )
                            }
                        }

                        // Icône centrale
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .shadow(12.dp, CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = if (showSuccessMessage) {
                                            listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                                        } else {
                                            listOf(Primary, PrimaryContainer)
                                        }
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = showSuccessMessage,
                                transitionSpec = {
                                    (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                                },
                                label = "iconTransition"
                            ) { success ->
                                Icon(
                                    imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.MarkEmailRead,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // === TITRE ET DESCRIPTION ===
                    AnimatedContent(
                        targetState = showSuccessMessage,
                        transitionSpec = {
                            slideInVertically { it / 2 } + fadeIn() togetherWith
                                    slideOutVertically { -it / 2 } + fadeOut()
                        },
                        label = "textTransition"
                    ) { success ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (success) {
                                Text(
                                    text = "Email vérifié ! ✓",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = Color(0xFF4CAF50),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Parfait ! Votre compte est maintenant activé.\nRedirection en cours...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = "Vérifiez votre email",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = Color(0xFF1C1B1F),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Un email de confirmation a été envoyé à :",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Primary.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = currentUser?.email ?: "email@example.com",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Primary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            // === CONTENU (masqué si vérifié) ===
            if (!showSuccessMessage) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 400))
                ) {
                    Column {
                        // Instructions Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Primary.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Primary.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                Brush.linearGradient(listOf(Primary, PrimaryContainer)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Instructions",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Primary
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                InstructionStep("1", "Ouvrez votre application email")
                                Spacer(Modifier.height(8.dp))
                                InstructionStep("2", "Cherchez un email de MyProd")
                                Spacer(Modifier.height(8.dp))
                                InstructionStep("3", "Cliquez sur le lien de confirmation")
                                Spacer(Modifier.height(8.dp))
                                InstructionStep("4", "La vérification sera automatique !")
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Bouton Vérifier
                        Button(
                            onClick = {
                                viewModel.refreshEmailVerificationStatus()
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(listOf(Primary, PrimaryContainer)),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier
                                            .size(32.dp)
                                            .background(Color.White.copy(0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CheckCircle, null, Modifier.size(20.dp), tint = Color.White)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "J'ai vérifié mon email",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Bouton Renvoyer
                        OutlinedButton(
                            onClick = {
                                viewModel.resendVerificationEmail {
                                    resendTimer = 60
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = resendTimer == 0 && !isLoading,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Primary,
                                disabledContentColor = Color.Gray
                            ),
                            border = BorderStroke(2.dp, if (resendTimer == 0) Primary else Color.Gray.copy(0.3f))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(Modifier.size(24.dp), color = Primary, strokeWidth = 3.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, null, Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (resendTimer > 0) "Renvoyer dans ${resendTimer}s" else "Renvoyer l'email",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        // Divider
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(Modifier.weight(1f), color = Color.LightGray.copy(0.3f))
                            Text(" ou ", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
                            HorizontalDivider(Modifier.weight(1f), color = Color.LightGray.copy(0.3f))
                        }

                        Spacer(Modifier.height(24.dp))

                        // Email incorrect
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Email incorrect ?",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Créez un nouveau compte avec la bonne adresse email.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(16.dp))
                                TextButton(onClick = onSwitchToSignup) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Créer un nouveau compte", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Default.ArrowForward, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InstructionStep(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(28.dp)
                .background(Primary.copy(0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1C1B1F))
    }
}
