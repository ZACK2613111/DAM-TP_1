package com.example.dam_tp_1.model

import androidx.compose.ui.graphics.Color
import com.example.dam_tp_1.R
import com.example.dam_tp_1.ui.theme.ProductConsumable
import com.example.dam_tp_1.ui.theme.ProductDurable
import com.example.dam_tp_1.ui.theme.ProductOther

enum class ProductType(val displayName: String, val imageRes: Int, val accentColor: Color) {
    Consumable("Consommable", R.drawable.black_shirt, ProductConsumable), // ✅ Vert nature
    Durable("Durable", R.drawable.macbook, ProductDurable),               // ✅ Bleu tech
    Other("Autre", R.drawable.chair, ProductOther)                        // ✅ Orange créatif
}
