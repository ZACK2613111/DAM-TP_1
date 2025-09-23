package com.example.dam_tp_1.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dam_tp_1.screens.*

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Step1.route,
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
        composable(Screen.Step1.route) {
            Step1Screen(navController)
        }
        composable(Screen.Step2.route) {
            Step2Screen(navController)
        }
        composable(Screen.Step3.route) {
            Step3Screen(navController)
        }
        composable(Screen.Summary.route) {
            SummaryScreen(navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Step1 : Screen("step1")
    object Step2 : Screen("step2")
    object Step3 : Screen("step3")
    object Summary : Screen("summary")
}
