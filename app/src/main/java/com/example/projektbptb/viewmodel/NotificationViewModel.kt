package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.network.NotificationResponse
import com.example.projektbptb.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val notificationRepository = NotificationRepository()
    
    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadNotifications()
        loadUnreadCount()
    }
    
    fun loadNotifications() {
        val token = authRepository.getToken()
        if (token == null) {
            android.util.Log.e("NotificationViewModel", "No token found")
            _errorMessage.value = "Silakan login untuk melihat notifikasi"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        android.util.Log.d("NotificationViewModel", "Loading notifications...")
        
        viewModelScope.launch {
            notificationRepository.getNotifications(token)
                .onSuccess { notificationList ->
                    android.util.Log.d("NotificationViewModel", "Notifications loaded: ${notificationList.size}")
                    _notifications.value = notificationList
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    android.util.Log.e("NotificationViewModel", "Failed to load notifications: ${exception.message}", exception)
                    _errorMessage.value = exception.message ?: "Gagal memuat notifikasi"
                    _isLoading.value = false
                }
        }
    }
    
    fun loadUnreadCount() {
        val token = authRepository.getToken()
        if (token == null) {
            return
        }
        
        viewModelScope.launch {
            notificationRepository.getUnreadCount(token)
                .onSuccess { count ->
                    _unreadCount.value = count
                }
                .onFailure { exception ->
                    android.util.Log.e("NotificationViewModel", "Failed to load unread count: ${exception.message}")
                }
        }
    }
    
    fun markAsRead(notificationId: Int, onSuccess: () -> Unit = {}) {
        val token = authRepository.getToken()
        if (token == null) {
            _errorMessage.value = "Anda harus login untuk menandai notifikasi"
            return
        }
        
        viewModelScope.launch {
            notificationRepository.markAsRead(token, notificationId)
                .onSuccess {
                    // Update local state
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(is_read = true)
                        } else {
                            notification
                        }
                    }
                    // Reload unread count
                    loadUnreadCount()
                    onSuccess()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Gagal menandai notifikasi"
                }
        }
    }
    
    fun markAllAsRead(onSuccess: () -> Unit = {}) {
        val token = authRepository.getToken()
        if (token == null) {
            _errorMessage.value = "Anda harus login untuk menandai semua notifikasi"
            return
        }
        
        viewModelScope.launch {
            notificationRepository.markAllAsRead(token)
                .onSuccess {
                    // Update local state
                    _notifications.value = _notifications.value.map { it.copy(is_read = true) }
                    _unreadCount.value = 0
                    onSuccess()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Gagal menandai semua notifikasi"
                }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

