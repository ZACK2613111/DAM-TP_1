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
import com.example.dam_tp_1.auth.AuthScreen
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import com.example.dam_tp_1.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val sharedViewModel: ProductFormViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route, // ✅ Commence par Splash
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

        // ✅ ONBOARDING SCREEN (NOUVEAU!)
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }

        composable(Screen.Auth.route) {
            AuthScreen(navController, authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController, sharedViewModel, authViewModel)
        }

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

        composable(Screen.Step1.route) {
            Step1Screen(navController, sharedViewModel)
        }

        composable(Screen.Step2.route) {
            Step2Screen(navController, sharedViewModel)
        }

        composable(Screen.Step3.route) {
            Step3Screen(navController, sharedViewModel)
        }

        composable(Screen.Summary.route) {
            SummaryScreen(navController, sharedViewModel)
        }
    }
}

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding") // ✅ NOUVEAU!
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    data object Step1 : Screen("step1")
    data object Step2 : Screen("step2")
    data object Step3 : Screen("step3")
    data object Summary : Screen("summary")
}
