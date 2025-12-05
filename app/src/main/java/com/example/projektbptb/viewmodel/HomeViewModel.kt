package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.repository.ProductRepository
import com.example.projektbptb.data.model.Product
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository()
    private val authRepository = AuthRepository(application)
    
    val categories = listOf("All", "Baju", "Perabotan", "Elektronik", "Kulia", "Sepatu")
    val selectedCategory = mutableStateOf("All")
    
    val products = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    
    init {
        loadProducts()
    }
    
    fun loadProducts(category: String? = null, search: String? = null) {
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            val token = authRepository.getToken()
            val categoryFilter = if (category == "All" || category == null) null else category
            
            productRepository.getProducts(categoryFilter, search, token)
                .onSuccess { productList ->
                    products.clear()
                    products.addAll(productList)
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memuat produk"
                    isLoading.value = false
                }
        }
    }
    
    fun toggleFavorite(product: Product) {
        val token = authRepository.getToken()
        if (token == null) {
            errorMessage.value = "Silakan login terlebih dahulu"
            return
        }
        
        viewModelScope.launch {
            productRepository.toggleFavorite(product.id.toIntOrNull() ?: 0, token)
                .onSuccess { updatedProduct ->
                    val index = products.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        products[index] = updatedProduct
                    }
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal mengubah favorite"
                }
        }
    }
    
    fun selectCategory(category: String) {
        selectedCategory.value = category
        loadProducts(category)
    }
    
    // Get favorite products
    val favoriteProducts: List<Product>
        get() = products.filter { it.isFavorite }
    
    fun removeFavorite(product: Product) {
        toggleFavorite(product)
    }
    
    fun refresh() {
        loadProducts(selectedCategory.value)
    }
}
