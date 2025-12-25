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
    val latitude: Double? = null, // Latitude for map display
    val longitude: Double? = null, // Longitude for map display
    val whatsappNumber: String,
    @DrawableRes val imageRes: Int = 0, // For local fallback images
    val imageUrl: String? = null, // For API images (cover/legacy)
    val images: List<ProductImage> = emptyList(), // Multi-image support
    val isFavorite: Boolean = false,
    val sellerName: String = "Penjual",
    val sellerUsername: String? = null,
    val sellerProfileImage: String? = null,
    val userId: Int? = null, // ID pemilik produk
    val isOwnProduct: Boolean = false, // Flag untuk menandai produk milik user sendiri
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: String = "",
    val productId: Int = 0,
    val userId: Int = 0,
    val userName: String,
    val userUsername: String? = null,
    val userProfileImage: String? = null,
    val commentText: String,
    val parentCommentId: Int? = null,
    val replies: List<Comment> = emptyList(),
    val timestamp: String = "",
    val updatedAt: String = ""
)

