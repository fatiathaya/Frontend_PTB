package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.network.ChangePasswordRequest
import com.example.projektbptb.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val authApiService = NetworkModule.authApiService
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()
    
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        // Validasi
        if (currentPassword.isBlank()) {
            _errorMessage.value = "Password lama tidak boleh kosong"
            return
        }
        
        if (newPassword.isBlank()) {
            _errorMessage.value = "Password baru tidak boleh kosong"
            return
        }
        
        if (newPassword.length < 6) {
            _errorMessage.value = "Password baru minimal 6 karakter"
            return
        }
        
        if (newPassword != confirmPassword) {
            _errorMessage.value = "Konfirmasi password tidak sesuai"
            return
        }
        
        if (currentPassword == newPassword) {
            _errorMessage.value = "Password baru harus berbeda dengan password lama"
            return
        }
        
        val token = authRepository.getToken()
        if (token == null) {
            _errorMessage.value = "Anda harus login untuk mengubah password"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = false
        
        viewModelScope.launch {
            try {
                val request = ChangePasswordRequest(
                    current_password = currentPassword,
                    new_password = newPassword,
                    new_password_confirmation = confirmPassword
                )
                
                val response = authApiService.changePassword("Bearer $token", request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _isLoading.value = false
                    _isSuccess.value = true
                    _errorMessage.value = null
                    onSuccess()
                } else {
                    _isLoading.value = false
                    val errorMsg = response.body()?.message ?: "Gagal mengubah password"
                    _errorMessage.value = when {
                        errorMsg.contains("tidak sesuai", ignoreCase = true) -> "Password lama tidak sesuai"
                        errorMsg.contains("validation", ignoreCase = true) -> "Validasi gagal. Pastikan semua field terisi dengan benar"
                        else -> errorMsg
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("Unable to resolve host") == true -> "Tidak dapat terhubung ke server"
                    e.message?.contains("timeout") == true -> "Koneksi timeout. Periksa koneksi internet Anda"
                    else -> e.message ?: "Gagal mengubah password"
                }
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSuccess() {
        _isSuccess.value = false
    }
}

