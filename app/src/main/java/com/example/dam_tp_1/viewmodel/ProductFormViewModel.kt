package com.example.dam_tp_1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.example.dam_tp_1.data.ProductCondition
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.model.ProductType
import android.util.Log

class ProductFormViewModel : ViewModel() {

    var formData by mutableStateOf(ProductFormData())
        private set

    fun updateCustomImage(imageUri: String?) {
        formData = formData.copy(customImageUri = imageUri)
    }
    var productsList by mutableStateOf(listOf<ProductFormData>())
        private set

    private var nextId = 0

    fun updateFormData(update: (ProductFormData) -> ProductFormData) {
        formData = update(formData)
    }

    fun addProduct() {
        val newProduct = formData.copy()
        productsList = productsList + newProduct
        nextId++
        Log.d("ProductFormViewModel", "Produit ajouté: ${newProduct.productName}")
        Log.d("ProductFormViewModel", "Nombre total de produits: ${productsList.size}")
        resetForm()
    }

    // Supprimer un produit par référence
    fun removeProduct(product: ProductFormData) {
        productsList = productsList - product
        Log.d("ProductFormViewModel", "Produit supprimé: ${product.productName}")
        Log.d("ProductFormViewModel", "Nombre total de produits: ${productsList.size}")
    }

    // Supprimer un produit par index
    fun removeProductByIndex(index: Int) {
        if (index in 0 until productsList.size) {
            val product = productsList[index]
            productsList = productsList.filterIndexed { i, _ -> i != index }
            Log.d("ProductFormViewModel", "Produit supprimé par index $index: ${product.productName}")
            Log.d("ProductFormViewModel", "Nombre total de produits: ${productsList.size}")
        }
    }

    // Obtenir un produit par index
    fun getProductByIndex(index: Int): ProductFormData? {
        return productsList.getOrNull(index)
    }

    // Mettre à jour un produit existant
    fun updateProduct(index: Int, updatedProduct: ProductFormData) {
        if (index in 0 until productsList.size) {
            productsList = productsList.mapIndexed { i, product ->
                if (i == index) updatedProduct else product
            }
            Log.d("ProductFormViewModel", "Produit mis à jour à l'index $index: ${updatedProduct.productName}")
        }
    }

    // Toggle favori d'un produit
    fun toggleProductFavorite(index: Int) {
        if (index in 0 until productsList.size) {
            val product = productsList[index]
            val updatedProduct = product.copy(isFavorite = !product.isFavorite)
            updateProduct(index, updatedProduct)
            Log.d("ProductFormViewModel", "Favori basculé pour: ${product.productName}")
        }
    }

    // Réinitialiser le formulaire
    fun resetForm() {
        formData = ProductFormData()
        Log.d("ProductFormViewModel", "Formulaire réinitialisé")
    }

    // Validation Step 1
    fun isStep1Valid(): Boolean {
        return formData.productName.isNotBlank() &&
                formData.purchaseDate.isNotBlank()
    }

    // Validation Step 2
    fun isStep2Valid(): Boolean {
        return formData.brand.isNotBlank() &&
                formData.price.isNotBlank()
    }

    // Validation complète
    fun isFormComplete(): Boolean {
        return isStep1Valid() && isStep2Valid()
    }

    // Fonctions spécifiques pour chaque champ
    fun updateProductName(name: String) {
        formData = formData.copy(productName = name)
    }

    fun updatePurchaseDate(date: String) {
        formData = formData.copy(purchaseDate = date)
    }

    fun updateCountry(country: String) {
        formData = formData.copy(country = country)
    }

    fun updateBrand(brand: String) {
        formData = formData.copy(brand = brand)
    }

    fun updatePrice(price: String) {
        formData = formData.copy(price = price)
    }

    fun updateProductSize(size: String) {
        formData = formData.copy(productSize = size)
    }

    fun updateSelectedColor(colorArgb: Int) {
        formData = formData.copy(selectedColorArgb = colorArgb)
    }

    fun updateFavorite(isFavorite: Boolean) {
        formData = formData.copy(isFavorite = isFavorite)
    }

    fun updateNotes(notes: String) {
        formData = formData.copy(notes = notes)
    }

    fun updateRating(rating: Int) {
        formData = formData.copy(rating = rating)
    }

    fun updateWarranty(hasWarranty: Boolean) {
        formData = formData.copy(hasWarranty = hasWarranty)
    }

    fun updateWarrantyDuration(duration: String) {
        formData = formData.copy(warrantyDuration = duration)
    }

    fun updateCondition(condition: ProductCondition) {
        formData = formData.copy(condition = condition)
    }

    fun updateProductType(type: ProductType) {
        formData = formData.copy(selectedType = type)
    }

    // Statistiques
    fun getTotalValue(): Double {
        return productsList.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
    }

    fun getFavoriteCount(): Int {
        return productsList.count { it.isFavorite }
    }

    fun getProductsByType(type: ProductType): List<ProductFormData> {
        return productsList.filter { it.selectedType == type }
    }

    fun getProductsByCountry(country: String): List<ProductFormData> {
        return productsList.filter { it.country.equals(country, ignoreCase = true) }
    }

    fun getHighestRatedProducts(): List<ProductFormData> {
        return productsList.filter { it.rating >= 4 }.sortedByDescending { it.rating }
    }

    fun getMostExpensiveProducts(limit: Int = 5): List<ProductFormData> {
        return productsList.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }.take(limit)
    }

    fun searchProducts(query: String): List<ProductFormData> {
        if (query.isBlank()) return productsList

        return productsList.filter { product ->
            product.productName.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.country.contains(query, ignoreCase = true) ||
                    product.notes.contains(query, ignoreCase = true)
        }
    }

    // Import/Export des données (pour futures fonctionnalités)
    fun exportProductsToJson(): String {
        // Implémentation future pour exporter en JSON
        return ""
    }

    fun importProductsFromJson(json: String): Boolean {
        // Implémentation future pour importer depuis JSON
        return false
    }

    // Ajouter quelques produits d'exemple pour la démo
    init {
        addSampleProducts()
    }

    private fun addSampleProducts() {
        // Quelques produits d'exemple diversifiés pour tester les filtres
        val sampleProducts = listOf(
            ProductFormData(
                selectedType = ProductType.Durable,
                productName = "iPhone 15 Pro",
                brand = "Apple",
                price = "1199",
                purchaseDate = "15/09/2024",
                country = "États-Unis",
                selectedColorArgb = Color(0xFF007AFF).toArgb(), // Bleu Apple
                isFavorite = true,
                rating = 5,
                notes = "Excellent smartphone avec des performances exceptionnelles",
                hasWarranty = true,
                warrantyDuration = "1 an",
                condition = ProductCondition.New
            ),
            ProductFormData(
                selectedType = ProductType.Durable,
                productName = "MacBook Air M2",
                brand = "Apple",
                price = "1299",
                purchaseDate = "10/08/2024",
                country = "États-Unis",
                selectedColorArgb = Color(0xFFC0C0C0).toArgb(), // Argent
                isFavorite = true,
                rating = 5,
                hasWarranty = true,
                warrantyDuration = "2 ans",
                condition = ProductCondition.New,
                notes = "Parfait pour le développement et le design"
            ),
            ProductFormData(
                selectedType = ProductType.Consumable,
                productName = "Chemise Oxford",
                brand = "Zara",
                price = "39",
                purchaseDate = "02/09/2024",
                country = "Espagne",
                selectedColorArgb = Color(0xFF4169E1).toArgb(), // Bleu royal
                isFavorite = false,
                rating = 4,
                notes = "Très belle chemise, coupe parfaite",
                condition = ProductCondition.New
            ),
            ProductFormData(
                selectedType = ProductType.Durable,
                productName = "Vélo de Course",
                brand = "Trek",
                price = "899",
                purchaseDate = "20/07/2024",
                country = "États-Unis",
                selectedColorArgb = Color(0xFFFF4500).toArgb(), // Rouge orangé
                isFavorite = true,
                rating = 5,
                notes = "Excellent vélo, très léger et performant",
                hasWarranty = true,
                warrantyDuration = "5 ans",
                condition = ProductCondition.New,
                productSize = "56cm"
            ),
            ProductFormData(
                selectedType = ProductType.Consumable,
                productName = "Parfum Homme",
                brand = "Dior",
                price = "120",
                purchaseDate = "14/06/2024",
                country = "France",
                selectedColorArgb = Color(0xFF000000).toArgb(), // Noir
                isFavorite = true,
                rating = 4,
                notes = "Fragrance élégante et durable",
                condition = ProductCondition.New,
                productSize = "100ml"
            ),
            ProductFormData(
                selectedType = ProductType.Other,
                productName = "Plante Monstera",
                brand = "Local Garden",
                price = "25",
                purchaseDate = "03/05/2024",
                country = "France",
                selectedColorArgb = Color(0xFF228B22).toArgb(), // Vert forêt
                isFavorite = false,
                rating = 3,
                notes = "Plante d'intérieur décorative",
                condition = ProductCondition.Good,
                productSize = "Pot 20cm"
            ),
            ProductFormData(
                selectedType = ProductType.Durable,
                productName = "Casque Audio Sony",
                brand = "Sony",
                price = "299",
                purchaseDate = "18/04/2024",
                country = "Japon",
                selectedColorArgb = Color(0xFF2F2F2F).toArgb(), // Gris foncé
                isFavorite = true,
                rating = 5,
                notes = "Qualité audio exceptionnelle, réduction de bruit active",
                hasWarranty = true,
                warrantyDuration = "2 ans",
                condition = ProductCondition.New
            ),
            ProductFormData(
                selectedType = ProductType.Consumable,
                productName = "Sneakers Nike",
                brand = "Nike",
                price = "89",
                purchaseDate = "12/03/2024",
                country = "Vietnam",
                selectedColorArgb = Color(0xFFFFFFFF).toArgb(), // Blanc
                isFavorite = false,
                rating = 4,
                notes = "Confortables pour le sport et le quotidien",
                condition = ProductCondition.New,
                productSize = "42"
            )
        )

        productsList = sampleProducts
        nextId = sampleProducts.size
        Log.d("ProductFormViewModel", "Produits d'exemple ajoutés: ${productsList.size}")
    }
}
