package com.example.projektbptb.data.model

import androidx.annotation.DrawableRes

data class User(
    val id: Int = 0,
    val name: String = "User",
    val username: String? = null,
    val email: String = "",
    val phoneNumber: String? = null,
    val gender: String? = null, // "Laki-laki" or "Perempuan"
    @DrawableRes val profileImageRes: Int = 0, // For local fallback
    val profileImageUrl: String? = null // For API images
)
