package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.data.model.User
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun loadUserProfile(userId: Int) {
        if (userId <= 0) {
            _errorMessage.value = "User ID tidak valid"
            _isLoading.value = false
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        _user.value = null
        _products.value = emptyList()
        
        viewModelScope.launch {
            try {
                // Fetch User Profile
                val profileResult = authRepository.getUserProfile(userId)
                
                if (profileResult.isSuccess) {
                    val profileResponse = profileResult.getOrNull()
                    if (profileResponse != null) {
                        _user.value = User(
                            id = profileResponse.id,
                            name = profileResponse.name,
                            username = profileResponse.username,
                            email = profileResponse.email ?: "", // Restricted/Public
                            phoneNumber = profileResponse.phone_number,
                            gender = null, // Backend might not return gender or it was removed from model
                            profileImageUrl = profileResponse.profile_image
                        )
                    }
                } else {
                    _errorMessage.value = profileResult.exceptionOrNull()?.message ?: "Gagal memuat profil pengguna"
                    _isLoading.value = false
                    return@launch
                }
                
                // Fetch User Products
                val productRepository = ProductRepository()
                val productsResult = productRepository.getProducts(userId = userId)
                
                if (productsResult.isSuccess) {
                    _products.value = productsResult.getOrNull() ?: emptyList()
                } else {
                    // Log error but don't block profile display
                    // Maybe show a toast or partial error? 
                    // For now just empty list
                    _products.value = emptyList()
                }
                
                _isLoading.value = false
                
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message ?: "Unknown error"}"
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

