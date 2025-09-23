package com.example.dam_tp_1.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dam_tp_1.screens.*
import com.example.dam_tp_1.viewmodel.ProductFormViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    // ViewModel partagé pour toute l'application
    val sharedViewModel: ProductFormViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route, // ✅ COMMENCE PAR SPLASH
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 1000 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -1000 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // ✅ SPLASH SCREEN
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // ✅ AUTH SCREEN (Welcome + Login + Signup)
        composable(Screen.Auth.route) {
            AuthScreen(navController)
        }

        // Page d'accueil avec la liste des produits
        composable(Screen.Home.route) {
            HomeScreen(navController, sharedViewModel)
        }

        // Écran de détail du produit avec paramètre d'index
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            ProductDetailScreen(navController, sharedViewModel, productId)
        }

        // Formulaire d'ajout - Étape 1
        composable(Screen.Step1.route) {
            Step1Screen(navController, sharedViewModel)
        }

        // Formulaire d'ajout - Étape 2
        composable(Screen.Step2.route) {
            Step2Screen(navController, sharedViewModel)
        }

        // Formulaire d'ajout - Étape 3
        composable(Screen.Step3.route) {
            Step3Screen(navController, sharedViewModel)
        }

        // Écran de résumé avant validation
        composable(Screen.Summary.route) {
            SummaryScreen(navController, sharedViewModel)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash") // ✅ AJOUTÉ
    object Auth : Screen("auth")     // ✅ AJOUTÉ
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    object Step1 : Screen("step1")
    object Step2 : Screen("step2")
    object Step3 : Screen("step3")
    object Summary : Screen("summary")
}
