package com.example.dam_tp_1.model

import androidx.compose.ui.graphics.Color
import com.example.dam_tp_1.R
import com.example.dam_tp_1.ui.theme.PrimaryColor
import com.example.dam_tp_1.ui.theme.SecondaryColor
import com.example.dam_tp_1.ui.theme.Pink40

enum class ProductType(val displayName: String, val imageRes: Int, val accentColor: Color) {
    Consumable("Consommable", R.drawable.black_shirt, PrimaryColor),
    Durable("Durable", R.drawable.macbook, SecondaryColor),
    Other("Autre", R.drawable.chair, Pink40)
}
