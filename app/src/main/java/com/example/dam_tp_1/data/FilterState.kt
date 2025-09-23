package com.example.dam_tp_1.data

import com.example.dam_tp_1.model.ProductType

data class FilterState(
    val searchQuery: String = "",
    val selectedTypes: Set<ProductType> = emptySet(),
    val selectedCountries: Set<String> = emptySet(),
    val startDate: String = "",
    val endDate: String = "",
    val showFavoritesOnly: Boolean = false,
    val sortBy: SortOption = SortOption.DATE_DESC
)

enum class SortOption(val displayName: String) {
    NAME_ASC("Nom A-Z"),
    NAME_DESC("Nom Z-A"),
    DATE_ASC("Date croissante"),
    DATE_DESC("Date décroissante"),
    PRICE_ASC("Prix croissant"),
    PRICE_DESC("Prix décroissant"),
    RATING_DESC("Mieux notés")
}
