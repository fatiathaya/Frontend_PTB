package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val isLoginSuccess = mutableStateOf(false)
    
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Email dan password tidak boleh kosong"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            try {
                authRepository.login(email, password)
                    .onSuccess {
                        isLoading.value = false
                        isLoginSuccess.value = true
                        // Call onSuccess on main thread
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                    .onFailure { exception ->
                        isLoading.value = false
                        errorMessage.value = exception.message ?: "Login gagal"
                    }
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Error: ${e.message ?: e.javaClass.simpleName}"
            }
        }
    }
    
    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        username: String? = null,
        phoneNumber: String? = null,
        gender: String? = null,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage.value = "Nama, email, dan password harus diisi"
            return
        }
        
        if (password != confirmPassword) {
            errorMessage.value = "Password dan konfirmasi password tidak sama"
            return
        }
        
        if (password.length < 6) {
            errorMessage.value = "Password minimal 6 karakter"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            try {
                authRepository.register(name, email, password, confirmPassword, username, phoneNumber, gender)
                    .onSuccess {
                        isLoading.value = false
                        isLoginSuccess.value = true
                        // Call onSuccess on main thread
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                    .onFailure { exception ->
                        isLoading.value = false
                        errorMessage.value = exception.message ?: "Registrasi gagal"
                    }
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Error: ${e.message ?: e.javaClass.simpleName}"
            }
        }
    }
    
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
    
    fun getToken(): String? = authRepository.getToken()
}

