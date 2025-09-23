package com.example.dam_tp_1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dam_tp_1.data.ProductFormData

class ProductFormViewModel : ViewModel() {

    // État du formulaire avec mutableStateOf pour recomposition automatique
    var formData by mutableStateOf(ProductFormData())
        private set

    // Fonction pour mettre à jour les données du formulaire
    fun updateFormData(update: (ProductFormData) -> ProductFormData) {
        formData = update(formData)
    }

    // Réinitialiser le formulaire
    fun resetForm() {
        formData = ProductFormData()
    }

    // Validation Step 1 - Champs obligatoires de base
    fun isStep1Valid(): Boolean {
        return formData.productName.isNotBlank() &&
                formData.purchaseDate.isNotBlank()
    }

    // Validation Step 2 - Champs obligatoires commerciaux
    fun isStep2Valid(): Boolean {
        return formData.brand.isNotBlank() &&
                formData.price.isNotBlank()
    }

    // Validation complète du formulaire
    fun isFormComplete(): Boolean {
        return isStep1Valid() && isStep2Valid()
    }

    // Fonctions spécifiques pour chaque champ (optionnel, plus facile à utiliser)
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
}
