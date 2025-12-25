  package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.repository.ProductRepository
import kotlinx.coroutines.launch
import java.io.File

class AddProductViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val productRepository = ProductRepository()
    
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val isProductCreated = mutableStateOf(false)
    
    fun createProduct(
        name: String,
        category: String,
        condition: String,
        description: String,
        address: String?,
        latitude: Double?,
        longitude: Double?,
        price: String,
        whatsappNumber: String,
        imageFiles: List<File>
    ) {
        val token = authRepository.getToken() ?: run {
            errorMessage.value = "Anda belum login"
            return
        }
        
        // Validation
        if (name.isBlank()) {
            errorMessage.value = "Nama produk tidak boleh kosong"
            return
        }
        if (category.isBlank()) {
            errorMessage.value = "Kategori harus dipilih"
            return
        }
        if (condition.isBlank()) {
            errorMessage.value = "Kondisi harus dipilih"
            return
        }
        if (price.isBlank()) {
            errorMessage.value = "Harga tidak boleh kosong"
            return
        }
        if (whatsappNumber.isBlank()) {
            errorMessage.value = "Nomor WhatsApp tidak boleh kosong"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            productRepository.createProduct(
                name = name,
                category = category,
                price = price,
                description = description.takeIf { it.isNotBlank() },
                condition = condition,
                address = address?.takeIf { it.isNotBlank() },
                latitude = latitude,
                longitude = longitude,
                whatsappNumber = whatsappNumber,
                imageFile = null,
                imageFiles = imageFiles,
                token = token
            )
                .onSuccess {
                    isLoading.value = false
                    isProductCreated.value = true
                    errorMessage.value = null
                }
                .onFailure { exception ->
                    isLoading.value = false
                    errorMessage.value = exception.message ?: "Gagal membuat produk"
                    isProductCreated.value = false
                }
        }
    }
    
    fun resetState() {
        isProductCreated.value = false
        errorMessage.value = null
    }
}

