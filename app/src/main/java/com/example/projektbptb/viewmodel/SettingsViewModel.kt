package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.R
import com.example.projektbptb.data.model.User
import com.example.projektbptb.data.network.AuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    val user = mutableStateOf(
        User(
            name = "Jiharmok",
            email = "jiharmokgaming@gmail.com",
            profileImageRes = R.drawable.profil // ganti sesuai aset
        )
    )

    val isNotificationEnabled = mutableStateOf(true)
    val isLoading = mutableStateOf(false)
    val currentUser = mutableStateOf<User?>(null)
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getUser()
                .onSuccess { userResponse ->
                    currentUser.value = User(
                        id = userResponse.id,
                        name = userResponse.name,
                        username = userResponse.username,
                        email = userResponse.email,
                        phoneNumber = userResponse.phone_number,
                        gender = userResponse.gender,
                        profileImageUrl = userResponse.profile_image
                    )
                }
        }
    }
    
    fun isProfileComplete(): Boolean {
        return currentUser.value?.let { user ->
            !user.email.isNullOrBlank() && !user.phoneNumber.isNullOrBlank()
        } ?: false
    }

    fun toggleNotification() {
        isNotificationEnabled.value = !isNotificationEnabled.value
    }
    
    fun logout(onLogoutComplete: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    isLoading.value = false
                    onLogoutComplete()
                }
                .onFailure {
                    // Even if API call fails, clear local data and logout
                    isLoading.value = false
                    onLogoutComplete()
                }
        }
    }
}
