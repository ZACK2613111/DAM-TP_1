package com.example.dam_tp_1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dam_tp_1.data.FilterState
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.data.SortOption
import com.example.dam_tp_1.model.ProductType
import java.text.SimpleDateFormat
import java.util.*

class SearchFilterViewModel : ViewModel() {

    var filterState by mutableStateOf(FilterState())
        private set

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    fun updateSearchQuery(query: String) {
        filterState = filterState.copy(searchQuery = query)
    }

    fun toggleProductType(type: ProductType) {
        val updatedTypes = if (type in filterState.selectedTypes) {
            filterState.selectedTypes - type
        } else {
            filterState.selectedTypes + type
        }
        filterState = filterState.copy(selectedTypes = updatedTypes)
    }

    fun toggleCountry(country: String) {
        val updatedCountries = if (country in filterState.selectedCountries) {
            filterState.selectedCountries - country
        } else {
            filterState.selectedCountries + country
        }
        filterState = filterState.copy(selectedCountries = updatedCountries)
    }

    fun updateDateRange(startDate: String, endDate: String) {
        filterState = filterState.copy(startDate = startDate, endDate = endDate)
    }

    fun toggleFavoritesOnly() {
        filterState = filterState.copy(showFavoritesOnly = !filterState.showFavoritesOnly)
    }

    fun updateSortOption(sortOption: SortOption) {
        filterState = filterState.copy(sortBy = sortOption)
    }

    fun clearAllFilters() {
        filterState = FilterState()
    }

    fun getFilteredProducts(products: List<ProductFormData>): List<ProductFormData> {
        var filtered = products

        // Filtre par recherche
        if (filterState.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.productName.contains(filterState.searchQuery, ignoreCase = true) ||
                        it.brand.contains(filterState.searchQuery, ignoreCase = true)
            }
        }

        // Filtre par type
        if (filterState.selectedTypes.isNotEmpty()) {
            filtered = filtered.filter { it.selectedType in filterState.selectedTypes }
        }

        // Filtre par pays
        if (filterState.selectedCountries.isNotEmpty()) {
            filtered = filtered.filter { it.country in filterState.selectedCountries }
        }

        // Filtre par favoris
        if (filterState.showFavoritesOnly) {
            filtered = filtered.filter { it.isFavorite }
        }

        // Filtre par date
        if (filterState.startDate.isNotBlank() && filterState.endDate.isNotBlank()) {
            filtered = filtered.filter { product ->
                try {
                    val productDate = dateFormatter.parse(product.purchaseDate)
                    val startDate = dateFormatter.parse(filterState.startDate)
                    val endDate = dateFormatter.parse(filterState.endDate)
                    productDate != null && startDate != null && endDate != null &&
                            productDate.after(startDate) && productDate.before(endDate)
                } catch (e: Exception) {
                    true
                }
            }
        }

        // Tri
        filtered = when (filterState.sortBy) {
            SortOption.NAME_ASC -> filtered.sortedBy { it.productName }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.productName }
            SortOption.DATE_ASC -> filtered.sortedBy {
                try { dateFormatter.parse(it.purchaseDate) } catch (e: Exception) { Date(0) }
            }
            SortOption.DATE_DESC -> filtered.sortedByDescending {
                try { dateFormatter.parse(it.purchaseDate) } catch (e: Exception) { Date(0) }
            }
            SortOption.PRICE_ASC -> filtered.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
            SortOption.RATING_DESC -> filtered.sortedByDescending { it.rating }
        }

        return filtered
    }

    fun getAvailableCountries(products: List<ProductFormData>): List<String> {
        return products.mapNotNull { it.country.takeIf { country -> country.isNotBlank() } }
            .distinct()
            .sorted()
    }

    fun getActiveFiltersCount(): Int {
        var count = 0
        if (filterState.searchQuery.isNotBlank()) count++
        if (filterState.selectedTypes.isNotEmpty()) count++
        if (filterState.selectedCountries.isNotEmpty()) count++
        if (filterState.startDate.isNotBlank() || filterState.endDate.isNotBlank()) count++
        if (filterState.showFavoritesOnly) count++
        return count
    }
}
