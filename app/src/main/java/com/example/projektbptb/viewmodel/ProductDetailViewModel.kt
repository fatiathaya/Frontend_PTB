package com.example.projektbptb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.data.model.ProductDetail
import com.example.projektbptb.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {
    
    private val _productDetail = MutableStateFlow<ProductDetail?>(null)
    val productDetail: StateFlow<ProductDetail?> = _productDetail.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    val showAlert = MutableStateFlow(false)
    val alertMessage = MutableStateFlow<String?>(null)
    
    fun loadProductDetail(productId: Int, token: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            productRepository.getProduct(productId, token).fold(
                onSuccess = { product ->
                    _productDetail.value = product.toProductDetail()
                    _isFavorite.value = product.isFavorite
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load product"
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun toggleFavorite(productId: Int, token: String) {
        viewModelScope.launch {
            productRepository.toggleFavorite(productId, token).fold(
                onSuccess = { product ->
                    _isFavorite.value = product.isFavorite
                    // Update product detail if loaded
                    _productDetail.value?.let { current ->
                        _productDetail.value = current.copy(isFavorite = product.isFavorite)
                    }
                },
                onFailure = { exception ->
                    val message = exception.message ?: "Failed to toggle favorite"
                    _error.value = message
                    
                    // Tampilkan alert khusus jika user mencoba wishlist produk sendiri
                    if (message.contains("tidak bisa", ignoreCase = true) || 
                        message.contains("produk sendiri", ignoreCase = true)) {
                        alertMessage.value = "Tidak bisa wishlist produk sendiri"
                        showAlert.value = true
                    }
                }
            )
        }
    }
    
    private fun Product.toProductDetail(): ProductDetail {
        // Use first image from images list, or fallback to imageUrl
        val mainImageUrl = images.firstOrNull()?.url ?: imageUrl
        
        return ProductDetail(
            id = id,
            name = name,
            category = category ?: "",
            condition = condition ?: "",
            price = if (price.startsWith("Rp ")) price else if (price.isNotEmpty()) "Rp $price" else "Rp 0",
            description = description ?: "",
            location = location ?: address ?: "", // Use location from database, fallback to address
            latitude = latitude, // Pass latitude for map display
            longitude = longitude, // Pass longitude for map display
            whatsappNumber = whatsappNumber ?: "",
            imageUrl = mainImageUrl,
            images = images, // Pass all images to ProductDetail
            isFavorite = isFavorite,
            sellerName = sellerName ?: "Penjual",
            userId = userId,
            isOwnProduct = isOwnProduct
        )
    }
}

