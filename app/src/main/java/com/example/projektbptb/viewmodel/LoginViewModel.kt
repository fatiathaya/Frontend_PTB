package com.example.projektbptb.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val isLoginSuccess = mutableStateOf(false)
    val isRegisterSuccess = mutableStateOf(false)
    
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
                        
                        // Get FCM token and send to server after successful login
                        getFcmTokenAndSendToServer()
                        
                        // Call onSuccess on main thread
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                    .onFailure { exception ->
                        isLoading.value = false
                        val errorMsg = exception.message ?: "Login gagal"
                        // Terjemahkan pesan error untuk sandi salah
                        errorMessage.value = when {
                            errorMsg.contains("Invalid credentials", ignoreCase = true) -> 
                                "Sandi yang dimasukkan salah"
                            errorMsg.contains("Invalid", ignoreCase = true) && 
                            errorMsg.contains("credential", ignoreCase = true) -> 
                                "Sandi yang dimasukkan salah"
                            else -> errorMsg
                        }
                    }
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Error: ${e.message ?: e.javaClass.simpleName}"
            }
        }
    }
    
    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        name: String? = null,
        username: String? = null,
        phoneNumber: String? = null,
        gender: String? = null,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage.value = "Email dan password harus diisi"
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
                // Parameter name sekarang optional dan dipindah posisinya
                authRepository.register(
                    email = email,
                    password = password,
                    passwordConfirmation = confirmPassword,
                    name = name,
                    username = username,
                    phoneNumber = phoneNumber,
                    gender = gender
                )
                    .onSuccess {
                        isLoading.value = false
                        isLoading.value = false
                        isRegisterSuccess.value = true
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
    
    private fun getFcmTokenAndSendToServer() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("LoginViewModel", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("LoginViewModel", "FCM Registration Token: $token")

            // Send token to server
            viewModelScope.launch {
                authRepository.saveFcmToken(token)
                    .onSuccess {
                        Log.d("LoginViewModel", "FCM token berhasil dikirim ke server setelah login")
                    }
                    .onFailure { exception ->
                        Log.e("LoginViewModel", "Gagal mengirim FCM token ke server: ${exception.message}")
                    }
            }
        }
    }
}

