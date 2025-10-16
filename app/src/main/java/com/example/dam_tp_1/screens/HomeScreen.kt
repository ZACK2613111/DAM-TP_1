package com.example.dam_tp_1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dam_tp_1.components.*
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.ui.theme.*
import com.example.dam_tp_1.viewmodel.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductFormViewModel,
    authViewModel: AuthViewModel
) {
    val searchFilterViewModel: SearchFilterViewModel = viewModel()
    val allProducts = viewModel.productsList
    val filteredProducts = searchFilterViewModel.getFilteredProducts(allProducts)
    val availableCountries = searchFilterViewModel.getAvailableCountries(allProducts)
    val haptic = LocalHapticFeedback.current

    // Observer les données utilisateur
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf<ProductFormData?>(null) }
    var showUserMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(0) }

    val itemsPerPage = 3
    val totalPages = (filteredProducts.size + itemsPerPage - 1) / itemsPerPage
    val paginatedProducts = filteredProducts.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                Box {
                    CollectionTopBar(
                        totalProducts = allProducts.size,
                        filteredCount = filteredProducts.size,
                        onUserMenuClick = { showUserMenu = true }
                    )

                    UserProfileMenu(
                        userName = currentUser?.displayName ?: "Utilisateur",
                        userEmail = currentUser?.email ?: "email@example.com",
                        expanded = showUserMenu,
                        onDismiss = { showUserMenu = false },
                        onLogout = {
                            showUserMenu = false
                            showLogoutDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.Step1.route)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Primary, PrimaryContainer)
                                ),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "Ajouter",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            if (allProducts.isEmpty()) {
                EmptyCollectionState(
                    onAddProduct = {
                        navController.navigate(Screen.Step1.route)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    SearchBar(
                        query = searchFilterViewModel.filterState.searchQuery,
                        onQueryChange = searchFilterViewModel::updateSearchQuery,
                        onFilterClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        activeFiltersCount = searchFilterViewModel.getActiveFiltersCount(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    FilterChips(
                        filterState = searchFilterViewModel.filterState,
                        availableCountries = availableCountries,
                        onTypeToggle = searchFilterViewModel::toggleProductType,
                        onCountryToggle = searchFilterViewModel::toggleCountry,
                        onFavoritesToggle = searchFilterViewModel::toggleFavoritesOnly,
                        onSortChange = searchFilterViewModel::updateSortOption,
                        onClearFilters = {
                            searchFilterViewModel.clearAllFilters()
                            currentPage = 0
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    if (filteredProducts.isEmpty()) {
                        NoResultsState(
                            onClearFilters = {
                                searchFilterViewModel.clearAllFilters()
                                currentPage = 0
                            },
                            onAddProduct = {
                                navController.navigate(Screen.Step1.route)
                            }
                        )
                    } else {
                        ProductList(
                            products = paginatedProducts,
                            allProducts = allProducts,
                            currentPage = currentPage,
                            totalPages = totalPages,
                            itemsPerPage = itemsPerPage,
                            showStats = searchFilterViewModel.getActiveFiltersCount() == 0,
                            onProductClick = { index ->
                                val actualIndex = currentPage * itemsPerPage + index
                                navController.navigate(Screen.ProductDetail.createRoute(actualIndex))
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onProductDelete = { showDeleteDialog = it },
                            onPageChange = { newPage ->
                                currentPage = newPage
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        )
                    }
                }
            }

            // Delete Dialog
            showDeleteDialog?.let { product ->
                DeleteProductDialog(
                    product = product,
                    onConfirm = {
                        viewModel.removeProduct(product)
                        showDeleteDialog = null
                        if (paginatedProducts.size == 1 && currentPage > 0) {
                            currentPage--
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDismiss = { showDeleteDialog = null }
                )
            }

            // Logout Dialog
            if (showLogoutDialog) {
                LogoutDialog(
                    userName = currentUser?.displayName ?: "Utilisateur",
                    onConfirm = {
                        showLogoutDialog = false
                        authViewModel.logout {
                            navController.navigate(Screen.Auth.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDismiss = { showLogoutDialog = false }
                )
            }
        }
    }
}
