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
            productRepository.getMyProducts(token)
                .onSuccess { products ->
                    myProducts.clear()
                    myProducts.addAll(products)
                    productErrorMessage.value = null
                }
                .onFailure { exception ->
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
        gender: String
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
            
            authRepository.updateUser(updatedUser)
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
    
    fun deleteProduct(product: Product) {
        val token = authRepository.getToken() ?: return
        
        isUpdating.value = true
        successMessage.value = null
        errorMessage.value = null
        
        viewModelScope.launch {
            productRepository.deleteProduct(product.id.toIntOrNull() ?: 0, token)
                .onSuccess {
                    // Remove from local list immediately
                    myProducts.remove(product)
                    
                    // Reload all products to ensure fresh data from server
                    loadMyProducts()
                    
                    successMessage.value = "Produk berhasil dihapus"
                    isUpdating.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal menghapus produk"
                    isUpdating.value = false
                }
        }
    }
    
    fun updateProduct(oldProduct: Product, newProduct: Product, imageFile: java.io.File? = null) {
        val token = authRepository.getToken() ?: return
        
        isUpdating.value = true
        successMessage.value = null
        errorMessage.value = null
        
        viewModelScope.launch {
            productRepository.updateProduct(
                id = oldProduct.id.toIntOrNull() ?: 0,
                name = newProduct.name,
                category = newProduct.category,
                price = newProduct.price,
                description = newProduct.description,
                condition = newProduct.condition,
                whatsappNumber = newProduct.whatsappNumber,
                imageFile = imageFile,
                token = token
            )
                .onSuccess { updatedProduct ->
                    // Update local list immediately
                    val index = myProducts.indexOf(oldProduct)
                    if (index != -1) {
                        myProducts[index] = updatedProduct
                    }
                    
                    // Reload all products to ensure fresh data from server
                    loadMyProducts()
                    
                    successMessage.value = "Produk berhasil diperbarui"
                    isUpdating.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memperbarui produk"
                    isUpdating.value = false
                }
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

