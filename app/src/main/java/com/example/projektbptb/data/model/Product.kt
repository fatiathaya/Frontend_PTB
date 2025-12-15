package com.example.projektbptb.data.model

import androidx.annotation.DrawableRes

data class Product(
    val id: String = "",
    val name: String,
    val category: String,
    val price: String,
    @DrawableRes val imageRes: Int = 0, // For local fallback images
    val imageUrl: String? = null, // For API images
    val isFavorite: Boolean = false,
    val description: String? = null,
    val condition: String? = null,
    val whatsappNumber: String? = null,
    val sellerName: String? = null
)
