package com.example.dam_tp_1.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import kotlinx.coroutines.launch

// === MODÈLE DE PAGE ONBOARDING ===
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Définition des 3 pages
    val pages = listOf(
        OnboardingPage(
            title = "Gérez vos Produits",
            description = "Organisez et suivez tous vos produits en un seul endroit. Ajoutez des photos, des détails et bien plus encore.",
            icon = Icons.Default.Inventory2,
            gradient = listOf(Primary, PrimaryContainer)
        ),
        OnboardingPage(
            title = "Marquez vos Favoris",
            description = "Créez votre collection personnalisée en marquant vos produits préférés pour un accès rapide.",
            icon = Icons.Default.Star,
            gradient = listOf(Secondary, SecondaryContainer)
        ),
        OnboardingPage(
            title = "Suivez la Valeur",
            description = "Visualisez la valeur totale de votre collection et gérez votre budget efficacement.",
            icon = Icons.Default.TrendingUp,
            gradient = listOf(Tertiary, TertiaryContainer)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // === SKIP BUTTON ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Passer",
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // === HORIZONTAL PAGER ===
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // === DOTS INDICATOR ===
            PageIndicator(
                numberOfPages = pages.size,
                selectedPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 24.dp)
            )

            // === NAVIGATION BUTTONS ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton Précédent
                if (pagerState.currentPage > 0) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Précédent",
                            tint = Primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Précédent", color = Primary)
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                // Bouton Suivant / Commencer
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // Dernière page -> naviguer vers Auth
                            navController.navigate(Screen.Auth.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage < pages.size - 1) "Suivant" else "Commencer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = if (pagerState.currentPage < pages.size - 1)
                            Icons.Default.ArrowForward
                        else
                            Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // === ICON AVEC GRADIENT ===
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Brush.radialGradient(page.gradient),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.height(48.dp))

        // === TITRE ===
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            ),
            color = Primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // === DESCRIPTION ===
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

// === DOTS INDICATOR ANIMÉ ===
@Composable
fun PageIndicator(
    numberOfPages: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier,
    selectedColor: Color = Primary,
    defaultColor: Color = Color.LightGray,
    defaultRadius: Dp = 8.dp,
    selectedLength: Dp = 32.dp,
    space: Dp = 8.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space),
        modifier = modifier
    ) {
        repeat(numberOfPages) { index ->
            PageIndicatorDot(
                isSelected = index == selectedPage,
                selectedColor = selectedColor,
                defaultColor = defaultColor,
                defaultRadius = defaultRadius,
                selectedLength = selectedLength
            )
        }
    }
}

@Composable
fun PageIndicatorDot(
    isSelected: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    defaultRadius: Dp,
    selectedLength: Dp
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) selectedLength else defaultRadius,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "width"
    )

    Box(
        modifier = Modifier
            .height(defaultRadius)
            .width(width)
            .clip(CircleShape)
            .background(if (isSelected) selectedColor else defaultColor)
    )
}
