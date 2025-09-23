package com.example.dam_tp_1.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dam_tp_1.model.ProductType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductFormData(
    // Page 1 - Informations de base
    var selectedType: ProductType = ProductType.Consumable,
    var productName: String = "",
    var purchaseDate: String = "",
    var country: String = "",

    // Page 2 - Apparence & Détails
    var selectedColorArgb: Int = Color(0xFF6750A4).toArgb(),
    var productSize: String = "",
    var brand: String = "",
    var price: String = "",
    var condition: ProductCondition = ProductCondition.New,

    // Page 3 - Préférences & Validation
    var isFavorite: Boolean = false,
    var notes: String = "",
    var rating: Int = 5,
    var hasWarranty: Boolean = false,
    var warrantyDuration: String = ""
) : Parcelable

@Parcelize
enum class ProductCondition(val displayName: String) : Parcelable {
    New("Neuf"),
    LikeNew("Comme neuf"),
    Good("Bon état"),
    Fair("État correct"),
    Poor("Mauvais état")
}
