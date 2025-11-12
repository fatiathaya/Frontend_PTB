package com.example.projektbptb.model

import androidx.annotation.DrawableRes

data class Product(
    val name: String,
    val category: String,
    val price: String,
    @DrawableRes val imageRes: Int,
    val isFavorite: Boolean = false
)
