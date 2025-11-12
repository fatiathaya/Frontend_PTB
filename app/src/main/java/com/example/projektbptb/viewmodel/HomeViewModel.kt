package com.example.projektbptb.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.projektbptb.R
import com.example.projektbptb.model.Product

class HomeViewModel : ViewModel() {

    val categories = listOf("All", "Baju", "Perabotan", "Elektronik", "Kulia", "Sepatu")
    val selectedCategory = mutableStateOf("All")

    val products = mutableStateListOf(
        Product("Beruang", "Perabotan", "Rp 50.000", R.drawable.teddy, isFavorite = true),
        Product("Baju Cewek", "Baju", "Rp 120.000", R.drawable.dress, isFavorite = true),
        Product("Mainan", "Perabotan", "Rp 30.000", R.drawable.toy, isFavorite = true),
        Product("Sepatu Adidas", "Sepatu", "Rp 150.000", R.drawable.shoes, isFavorite = true),
        // Produk Baju tambahan
        Product("Baju Cewe Size Kid", "Baju", "Rp 50.000", R.drawable.dress, isFavorite = false),
        Product("Baju Kemeja", "Baju", "Rp 120.000", R.drawable.dress, isFavorite = false),
        Product("Kemeja Slim Fit", "Baju", "Rp 120.000", R.drawable.dress, isFavorite = false),
        Product("Kemeja Cowo", "Baju", "Rp 90.000", R.drawable.dress, isFavorite = false)
    )

    fun toggleFavorite(product: Product) {
        val index = products.indexOf(product)
        if (index != -1) {
            val updated = product.copy(isFavorite = !product.isFavorite)
            products[index] = updated
        }
    }

    fun selectCategory(category: String) {
        selectedCategory.value = category
    }

    // Get favorite products
    val favoriteProducts: List<Product>
        get() = products.filter { it.isFavorite }

    fun removeFavorite(product: Product) {
        val index = products.indexOf(product)
        if (index != -1) {
            val updated = product.copy(isFavorite = false)
            products[index] = updated
        }
    }
}
