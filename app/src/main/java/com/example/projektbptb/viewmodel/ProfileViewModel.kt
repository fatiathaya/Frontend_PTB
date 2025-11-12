package com.example.projektbptb.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.projektbptb.R
import com.example.projektbptb.model.Product
import com.example.projektbptb.model.User

class ProfileViewModel : ViewModel() {

    val user = mutableStateOf(
        User(
            name = "Jiharmok",
            username = "Jiharmok",
            email = "jiharmokgaming@gmail.com",
            phoneNumber = "08568908000",
            gender = "Laki-Laki"
        )
    )

    val isNotificationEnabled = mutableStateOf(true)

    // List produk yang ditambahkan user
    val myProducts = mutableStateListOf(
        Product("Sepatu Adidas", "Sepatu", "Rp 150.000", R.drawable.shoes),
        Product("Baju Cewek", "Baju", "Rp 120.000", R.drawable.dress)
    )

    fun toggleNotification() {
        isNotificationEnabled.value = !isNotificationEnabled.value
    }

    fun updateProfile(
        name: String,
        username: String,
        email: String,
        phoneNumber: String,
        gender: String
    ) {
        user.value = user.value.copy(
            name = name,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender
        )
    }

    fun deleteProduct(product: Product) {
        myProducts.remove(product)
    }

    fun updateProduct(oldProduct: Product, newProduct: Product) {
        val index = myProducts.indexOf(oldProduct)
        if (index != -1) {
            myProducts[index] = newProduct
        }
    }
}

