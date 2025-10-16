package com.example.dam_tp_1.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dam_tp_1.data.ProductCondition
import com.example.dam_tp_1.data.ProductFormData
import com.example.dam_tp_1.model.ProductType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductFormViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var formData by mutableStateOf(ProductFormData())
        private set

    var productsList by mutableStateOf<List<ProductFormData>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadUserProducts()
    }

    fun loadUserProducts() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                isLoading = true
                println("🔍 Chargement produits user: $userId")

                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("products")
                    .get()
                    .await()

                println("🔍 Documents trouvés: ${snapshot.documents.size}")

                productsList = snapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null

                        ProductFormData(
                            firestoreId = doc.id,
                            userId = userId,
                            productName = data["productName"] as? String ?: "",
                            brand = data["brand"] as? String ?: "",
                            price = data["price"] as? String ?: "",
                            country = data["country"] as? String ?: "",
                            purchaseDate = data["purchaseDate"] as? String ?: "",
                            selectedType = ProductType.valueOf(data["selectedType"] as? String ?: "Durable"),
                            rating = (data["rating"] as? Long)?.toInt() ?: 0,
                            isFavorite = data["isFavorite"] as? Boolean ?: false,
                            notes = data["notes"] as? String ?: "",
                            hasWarranty = data["hasWarranty"] as? Boolean ?: false,
                            warrantyDuration = data["warrantyDuration"] as? String ?: "",
                            productSize = data["productSize"] as? String ?: "",
                            selectedColorArgb = (data["selectedColorArgb"] as? Long)?.toInt() ?: android.graphics.Color.BLUE,
                            condition = ProductCondition.valueOf(data["condition"] as? String ?: "New"),
                            customImageUri = data["customImageUri"] as? String
                            // createdAt retiré ou mis par défaut
                        )
                    } catch (e: Exception) {
                        println("⚠️ Erreur: ${e.message}")
                        null
                    }
                }

                println("✅ ${productsList.size} produits chargés")
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun addProduct() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                isLoading = true
                println("🔍 Ajout produit user: $userId")

                val productData = formData.copy(userId = userId)

                db.collection("users")
                    .document(userId)
                    .collection("products")
                    .add(productData)
                    .await()

                println("✅ Produit ajouté")
                resetForm()
                loadUserProducts()
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun removeProduct(product: ProductFormData) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                isLoading = true
                println("🔍 Suppression: ${product.firestoreId}")

                db.collection("users")
                    .document(userId)
                    .collection("products")
                    .document(product.firestoreId)
                    .delete()
                    .await()

                println("✅ Produit supprimé")
                loadUserProducts()
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun updateFormData(update: (ProductFormData) -> ProductFormData) {
        formData = update(formData)
    }

    fun updateProductName(value: String) { formData = formData.copy(productName = value) }
    fun updatePurchaseDate(value: String) { formData = formData.copy(purchaseDate = value) }
    fun updateCountry(value: String) { formData = formData.copy(country = value) }
    fun updateBrand(value: String) { formData = formData.copy(brand = value) }
    fun updatePrice(value: String) { formData = formData.copy(price = value) }
    fun updateProductSize(value: String) { formData = formData.copy(productSize = value) }
    fun updateSelectedColor(color: Int) { formData = formData.copy(selectedColorArgb = color) }
    fun updateFavorite(value: Boolean) { formData = formData.copy(isFavorite = value) }
    fun updateRating(value: Int) { formData = formData.copy(rating = value) }
    fun updateWarranty(value: Boolean) { formData = formData.copy(hasWarranty = value) }
    fun updateWarrantyDuration(value: String) { formData = formData.copy(warrantyDuration = value) }
    fun updateNotes(value: String) { formData = formData.copy(notes = value) }
    fun updateCustomImage(uri: Uri?) { formData = formData.copy(customImageUri = uri?.toString()) }

    fun isStep1Valid() = formData.productName.isNotBlank() && formData.purchaseDate.isNotBlank()
    fun isStep2Valid() = formData.brand.isNotBlank() && formData.price.isNotBlank()

    fun resetForm() {
        formData = ProductFormData()
    }

    fun clearError() {
        errorMessage = null
    }
}
