package com.example.dam_tp_1.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dam_tp_1.model.ProductType
import kotlinx.parcelize.Parcelize
data class ProductFormData(
    val selectedType: ProductType = ProductType.Durable,
    val productName: String = "",
    val purchaseDate: String = "",
    val country: String = "",
    val brand: String = "",
    val price: String = "",
    val productSize: String = "",
    val selectedColorArgb: Int = Color.Blue.toArgb(),
    val condition: ProductCondition = ProductCondition.New,
    val isFavorite: Boolean = false,
    val rating: Int = 0,
    val notes: String = "",
    val hasWarranty: Boolean = false,
    val warrantyDuration: String = "",
    val customImageUri: String? = null // ✅ NOUVEAU - Image personnalisée
)

@Parcelize
enum class ProductCondition(val displayName: String) : Parcelable {
    New("Neuf"),
    LikeNew("Comme neuf"),
    Good("Bon état"),
    Fair("État correct"),
    Poor("Mauvais état")
}
