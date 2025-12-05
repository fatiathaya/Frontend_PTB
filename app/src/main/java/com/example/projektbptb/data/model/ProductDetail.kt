package com.example.projektbptb.data.model

import androidx.annotation.DrawableRes

data class ProductDetail(
    val id: String = "",
    val name: String,
    val category: String,
    val condition: String,
    val price: String,
    val description: String,
    val location: String,
    val whatsappNumber: String,
    @DrawableRes val imageRes: Int = 0, // For local fallback images
    val imageUrl: String? = null, // For API images
    val isFavorite: Boolean = false,
    val sellerName: String = "Penjual",
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: String = "",
    val userName: String,
    val commentText: String,
    val timestamp: String = ""
)

