package com.example.dam_tp_1.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dam_tp_1.components.*
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.navigation.Screen
import com.example.dam_tp_1.viewmodel.ProductFormViewModel
import com.example.dam_tp_1.viewmodel.SearchFilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductFormViewModel
) {
    val searchFilterViewModel: SearchFilterViewModel = viewModel()
    val allProducts = viewModel.productsList
    val filteredProducts = searchFilterViewModel.getFilteredProducts(allProducts)
    val availableCountries = searchFilterViewModel.getAvailableCountries(allProducts)

    val haptic = LocalHapticFeedback.current
    var showDeleteDialog by remember { mutableStateOf<ProductFormData?>(null) }
    var showUserMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 3
    val totalPages = (filteredProducts.size + itemsPerPage - 1) / itemsPerPage
    val paginatedProducts = filteredProducts.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Ma Collection",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "${filteredProducts.size} produit${if (filteredProducts.size > 1) "s" else ""}" +
                                    if (filteredProducts.size != allProducts.size) " sur ${allProducts.size}" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { showUserMenu = true }
                        ) {
                            Card(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Menu utilisateur",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Menu utilisateur
                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false },
                            modifier = Modifier.width(220.dp)
                        ) {
                            // Header du menu avec info utilisateur
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        modifier = Modifier.size(48.dp),
                                        shape = CircleShape,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "John Doe",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            "john.doe@example.com",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // Badge premium ou stats
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Collection Premium",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            HorizontalDivider()

                            // Menu items
                            DropdownMenuItem(
                                text = { Text("Mon profil") },
                                onClick = {
                                    showUserMenu = false
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // TODO: Navigation vers profil
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Paramètres") },
                                onClick = {
                                    showUserMenu = false
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // TODO: Navigation vers paramètres
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Mes statistiques") },
                                onClick = {
                                    showUserMenu = false
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // TODO: Navigation vers stats
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Analytics,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Aide & Support") },
                                onClick = {
                                    showUserMenu = false
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // TODO: Navigation vers aide
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Help,
                                        contentDescription = null
                                    )
                                }
                            )

                            HorizontalDivider()

                            // Bouton déconnexion
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Se déconnecter",
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    showUserMenu = false
                                    showLogoutDialog = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Step1.route)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter un produit"
                    )
                },
                text = { Text("Ajouter") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        if (allProducts.isEmpty()) {
            // État vide initial
            EmptyState(
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
                // Barre de recherche
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            Text(
                                "Aucun produit trouvé",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Essayez de modifier vos critères de recherche ou ajoutez de nouveaux produits",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )

                            Spacer(Modifier.height(24.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        searchFilterViewModel.clearAllFilters()
                                        currentPage = 0
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ClearAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Effacer filtres")
                                }

                                ElevatedButton(
                                    onClick = {
                                        navController.navigate(Screen.Step1.route)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Ajouter produit")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (searchFilterViewModel.getActiveFiltersCount() == 0) {
                            item {
                                StatsHeader(allProducts)
                            }
                        }

                        itemsIndexed(
                            items = paginatedProducts,
                            key = { index, product -> "${product.productName}_${product.brand}_$index" }
                        ) { index, product ->
                            ProductCard(
                                product = product,
                                onClick = {
                                    val actualIndex = currentPage * itemsPerPage + index
                                    navController.navigate(Screen.ProductDetail.createRoute(actualIndex))
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                onDelete = {
                                    showDeleteDialog = product
                                }
                            )
                        }

                        if (totalPages > 1) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Bouton Précédent
                                        IconButton(
                                            onClick = {
                                                currentPage = currentPage - 1
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                            enabled = currentPage > 0
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChevronLeft,
                                                contentDescription = "Page précédente"
                                            )
                                        }

                                        // Indicateur de page
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "${currentPage + 1} / $totalPages",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                            )
                                        }

                                        // Bouton Suivant
                                        IconButton(
                                            onClick = {
                                                currentPage = currentPage + 1
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                            enabled = currentPage < totalPages - 1
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = "Page suivante"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Espace pour le FAB
                        item {
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        // Dialog de suppression
        showDeleteDialog?.let { product ->
            DeleteProductDialog(
                product = product,
                onConfirm = {
                    viewModel.removeProduct(product)
                    showDeleteDialog = null
                    // Ajuster la page si nécessaire
                    if (paginatedProducts.size == 1 && currentPage > 0) {
                        currentPage--
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onDismiss = {
                    showDeleteDialog = null
                }
            )
        }

        // Dialog de déconnexion
        if (showLogoutDialog) {
            LogoutDialog(
                showDialog = showLogoutDialog,
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    // Navigation vers l'écran d'auth
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }
    }
}

// ===== COMPOSANTS PRIVÉS =====

@Composable
private fun EmptyState(
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Votre collection est vide",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Commencez à construire votre collection en ajoutant votre premier produit",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        ElevatedButton(
            onClick = onAddProduct,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Ajouter mon premier produit",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        // Features preview
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FeaturePreview(
                icon = Icons.Default.Search,
                text = "Recherche"
            )
            FeaturePreview(
                icon = Icons.Default.Star,
                text = "Favoris"
            )
            FeaturePreview(
                icon = Icons.Default.Analytics,
                text = "Statistiques"
            )
        }
    }
}

@Composable
private fun FeaturePreview(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StatsHeader(productsList: List<ProductFormData>) {
    val favoriteCount = productsList.count { it.isFavorite }
    val totalValue = productsList.sumOf {
        it.price.toDoubleOrNull() ?: 0.0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Aperçu de votre collection",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Inventory,
                    value = productsList.size.toString(),
                    label = "Produits",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.Favorite,
                    value = favoriteCount.toString(),
                    label = "Favoris",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.Euro,
                    value = "${totalValue.toInt()}€",
                    label = "Valeur",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun DeleteProductDialog(
    product: ProductFormData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Card(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        title = {
            Text(
                "Supprimer le produit ?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                "Êtes-vous sûr de vouloir supprimer \"${product.productName}\" de votre collection ? Cette action est irréversible.",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            ElevatedButton(
                onClick = onConfirm,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun LogoutDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    "Confirmation de déconnexion",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        "Êtes-vous sûr de vouloir vous déconnecter ?",
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Vous devrez vous reconnecter pour accéder à votre collection.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    // ✅ BOUTONS EN LIGNE (ROW)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Bouton Annuler
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rester connecté")
                        }

                        // Bouton Confirmer
                        ElevatedButton(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Se déconnecter")
                        }
                    }
                }
            },
            confirmButton = { /* Vide car les boutons sont dans text */ },
            dismissButton = { /* Vide car les boutons sont dans text */ }
        )
    }
}

