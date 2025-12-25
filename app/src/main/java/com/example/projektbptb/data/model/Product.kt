package com.example.projektbptb.data.model

import androidx.annotation.DrawableRes

data class ProductImage(
    val id: Int,
    val url: String
)

data class Product(
    val id: String = "",
    val name: String,
    val category: String,
    val price: String,
    @DrawableRes val imageRes: Int = 0, // For local fallback images
    val imageUrl: String? = null, // For API images (cover/legacy)
    val images: List<ProductImage> = emptyList(), // Multi-image support
    val isFavorite: Boolean = false,
    val description: String? = null,
    val condition: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val location: String? = null, // Location field from database
    val whatsappNumber: String? = null,
    val sellerName: String? = null,
    val sellerUsername: String? = null,
    val sellerProfileImage: String? = null,
    val userId: Int? = null, // ID pemilik produk
    val isOwnProduct: Boolean = false // Flag untuk menandai produk milik user sendiri
)
