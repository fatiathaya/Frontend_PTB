package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.data.model.User
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val productRepository = ProductRepository()
    
    val user = mutableStateOf<User?>(null)
    val isNotificationEnabled = mutableStateOf(true)
    val myProducts = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    val isUpdating = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val productErrorMessage = mutableStateOf<String?>(null) // Separate error for products
    val successMessage = mutableStateOf<String?>(null)
    val deletingProductId = mutableStateOf<String?>(null) // Track which product is being deleted for animation
    
    // Function to clear all messages
    fun clearMessages() {
        successMessage.value = null
        errorMessage.value = null
        isUpdating.value = false
    }
    
    init {
        loadUser()
        loadMyProducts()
    }
    
    fun loadUser() {
        isLoading.value = true
        errorMessage.value = null // Clear previous errors
        viewModelScope.launch {
            authRepository.getUser()
                .onSuccess { userResponse ->
                    user.value = User(
                        id = userResponse.id,
                        name = userResponse.name,
                        username = userResponse.username,
                        email = userResponse.email,
                        phoneNumber = userResponse.phone_number,
                        gender = userResponse.gender,
                        profileImageUrl = userResponse.profile_image
                    )
                    errorMessage.value = null // Clear error on success
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memuat profil"
                    isLoading.value = false
                }
        }
    }
    
    fun loadMyProducts() {
        val token = authRepository.getToken() ?: return
        
        // Don't set isLoading to true here to avoid blocking UI
        // Only set productErrorMessage, not the main errorMessage
        viewModelScope.launch {
            android.util.Log.d("ProfileViewModel", "Loading my products from server...")
            productRepository.getMyProducts(token)
                .onSuccess { products ->
                    android.util.Log.d("ProfileViewModel", "Loaded ${products.size} products from server")
                    // Log each product's images for debugging
                    products.forEach { product ->
                        android.util.Log.d("ProfileViewModel", "Product: id=${product.id}, name=${product.name}, images=${product.images?.size ?: 0}, image IDs=${product.images?.map { it.id }}")
                    }
                    
                    myProducts.clear()
                    myProducts.addAll(products)
                    productErrorMessage.value = null
                    
                    android.util.Log.d("ProfileViewModel", "myProducts updated. Total: ${myProducts.size}")
                }
                .onFailure { exception ->
                    android.util.Log.e("ProfileViewModel", "Failed to load products: ${exception.message}")
                    // Only set product error, not main error message
                    productErrorMessage.value = exception.message ?: "Gagal memuat produk"
                    // Don't show this error in AddProfileScreen
                }
        }
    }
    
    fun toggleNotification() {
        isNotificationEnabled.value = !isNotificationEnabled.value
    }
    
    fun updateProfile(
        name: String,
        username: String,
        email: String,
        phoneNumber: String,
        gender: String,
        imageFile: java.io.File? = null
    ) {
        val currentUser = user.value ?: return
        
        isLoading.value = true
        errorMessage.value = null // Clear previous errors before update
        viewModelScope.launch {
            val updatedUser = currentUser.copy(
                name = name,
                username = username,
                email = email,
                phoneNumber = phoneNumber,
                gender = gender
            )
            
            // Use multipart update if image is provided, otherwise use regular update
            val result = if (imageFile != null) {
                authRepository.updateUserWithImage(updatedUser, imageFile)
            } else {
                authRepository.updateUser(updatedUser)
            }
            
            result
                .onSuccess { userResponse ->
                    user.value = User(
                        id = userResponse.id,
                        name = userResponse.name,
                        username = userResponse.username,
                        email = userResponse.email,
                        phoneNumber = userResponse.phone_number,
                        gender = userResponse.gender,
                        profileImageUrl = userResponse.profile_image
                    )
                    errorMessage.value = null // Clear error on success
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memperbarui profil"
                    isLoading.value = false
                }
        }
    }
    
    fun deleteProfileImage() {
        isLoading.value = true
        errorMessage.value = null
        successMessage.value = null
        
        viewModelScope.launch {
            authRepository.deleteProfileImage()
                .onSuccess { userResponse ->
                    // Update local user state
                    user.value = user.value?.copy(
                        profileImageUrl = null
                    )
                    successMessage.value = "Foto profil berhasil dihapus"
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal menghapus foto profil"
                    isLoading.value = false
                }
        }
    }
    
    fun deleteProductImage(productId: String, imageId: Int) {
        val token = authRepository.getToken() ?: return
        
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            productRepository.deleteProductImage(
                productId = productId.toInt(),
                imageId = imageId,
                token = token
            ).onSuccess { updatedProduct ->
                android.util.Log.d("ProfileViewModel", "=== IMAGE DELETE SUCCESS ===")
                android.util.Log.d("ProfileViewModel", "Deleted imageId=$imageId from productId=$productId")
                android.util.Log.d("ProfileViewModel", "Updated images: ${updatedProduct.images?.size ?: 0}, IDs=${updatedProduct.images?.map { it.id }}")
                
                // Update the product in myProducts list with new images
                val productIndex = myProducts.indexOfFirst { it.id == productId }
                if (productIndex >= 0) {
                    val existingProduct = myProducts[productIndex]
                    // Update product with new images list from server response
                    val updatedProductWithData = existingProduct.copy(
                        images = updatedProduct.images,
                        imageUrl = updatedProduct.images?.firstOrNull()?.url
                    )
                    myProducts[productIndex] = updatedProductWithData
                    android.util.Log.d("ProfileViewModel", "Product updated in list. New images count: ${updatedProductWithData.images?.size ?: 0}")
                } else {
                    android.util.Log.w("ProfileViewModel", "Product not found in list, reloading all products")
                    // If product not found, reload all products
                    loadMyProducts()
                }
                
                successMessage.value = "Foto berhasil dihapus"
            }.onFailure { error ->
                errorMessage.value = "Gagal menghapus foto: ${error.message}"
                android.util.Log.e("ProfileViewModel", "Failed to delete product image", error)
            }
            
            isLoading.value = false
        }
    }
    
    fun deleteProduct(product: Product) {
        val token = authRepository.getToken() ?: return
        
        // Mark product as deleting to trigger animation
        deletingProductId.value = product.id
        isUpdating.value = true
        successMessage.value = null
        errorMessage.value = null
        
        viewModelScope.launch {
            // Wait for animation to complete (500ms)
            kotlinx.coroutines.delay(500)
            
            productRepository.deleteProduct(product.id.toIntOrNull() ?: 0, token)
                .onSuccess {
                    // Remove from local list after animation
                    myProducts.remove(product)
                    
                    // Reload all products to ensure fresh data from server
                    loadMyProducts()
                    
                    successMessage.value = "Produk berhasil dihapus"
                    isUpdating.value = false
                    deletingProductId.value = null
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal menghapus produk"
                    isUpdating.value = false
                    deletingProductId.value = null
                }
        }
    }
    
    fun updateProduct(
        oldProduct: Product, 
        newProduct: Product, 
        imageFile: java.io.File? = null,
        imageFiles: List<java.io.File>? = null,
        deleteImageIds: List<Int>? = null
    ) {
        val token = authRepository.getToken()
        if (token == null) {
            errorMessage.value = "Silakan login terlebih dahulu"
            return
        }
        
        isUpdating.value = true
        successMessage.value = null
        errorMessage.value = null
        
        viewModelScope.launch {
            try {
                productRepository.updateProduct(
                    id = oldProduct.id.toIntOrNull() ?: 0,
                    name = newProduct.name,
                    category = newProduct.category,
                    price = newProduct.price,
                    description = newProduct.description,
                    condition = newProduct.condition,
                    address = newProduct.address,
                    latitude = newProduct.latitude,
                    longitude = newProduct.longitude,
                    whatsappNumber = newProduct.whatsappNumber,
                    imageFile = imageFile,
                    imageFiles = imageFiles,
                    deleteImageIds = deleteImageIds,
                    token = token
                )
                    .onSuccess { updatedProduct ->
                        android.util.Log.d("ProfileViewModel", "=== PRODUCT UPDATE SUCCESS ===")
                        android.util.Log.d("ProfileViewModel", "Updated product: id=${updatedProduct.id}, " +
                                "images=${updatedProduct.images?.size ?: 0}, " +
                                "image IDs=${updatedProduct.images?.map { it.id }}")
                        
                        // CRITICAL: Reset isUpdating FIRST to stop loading indicator
                        isUpdating.value = false
                        
                        // Update local list immediately with response from server
                        val index = myProducts.indexOfFirst { it.id == oldProduct.id }
                        if (index != -1) {
                            myProducts[index] = updatedProduct
                        } else {
                            myProducts.add(updatedProduct)
                        }
                        
                        // Set success message - this will trigger navigation back
                        successMessage.value = "Produk berhasil diperbarui"
                    }
                    .onFailure { exception ->
                        // CRITICAL: Reset isUpdating on failure to stop loading indicator
                        isUpdating.value = false
                        errorMessage.value = exception.message ?: "Gagal memperbarui produk"
                    }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Unexpected error in updateProduct: ${e.message}", e)
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
                // CRITICAL: Reset isUpdating on exception
                isUpdating.value = false
            }
            // Note: Remove finally block since we handle isUpdating in each case above
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            user.value = null
            myProducts.clear()
        }
    }
}

