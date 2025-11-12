package com.example.projektbptb.model

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
    @DrawableRes val imageRes: Int,
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

