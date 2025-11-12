package com.example.projektbptb.model

import androidx.annotation.DrawableRes

data class User(
    val name: String = "User",
    val username: String = "XXXXXXXXXXX",
    val email: String = "",
    val phoneNumber: String = "",
    val gender: String = "Laki-laki", // "Laki-laki" or "Perempuan"
    @DrawableRes val profileImageRes: Int = 0
)
