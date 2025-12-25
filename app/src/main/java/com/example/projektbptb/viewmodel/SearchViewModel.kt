package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.network.SearchHistoryResponse
import com.example.projektbptb.data.repository.SearchHistoryRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val searchHistoryRepository = SearchHistoryRepository()
    
    val searchHistory = mutableStateListOf<SearchHistoryResponse>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    
    init {
        loadSearchHistory()
    }
    
    fun loadSearchHistory() {
        val token = authRepository.getToken()
        if (token == null) {
            // User not logged in, skip loading history
            android.util.Log.d("SearchViewModel", "Token null - user not logged in")
            errorMessage.value = "Silakan login untuk melihat riwayat pencarian"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        android.util.Log.d("SearchViewModel", "Loading search history...")
        
        viewModelScope.launch {
            searchHistoryRepository.getSearchHistory(token)
                .onSuccess { histories ->
                    android.util.Log.d("SearchViewModel", "Loaded ${histories.size} search histories")
                    searchHistory.clear()
                    searchHistory.addAll(histories)
                    isLoading.value = false
                }
                .onFailure { exception ->
                    android.util.Log.e("SearchViewModel", "Failed to load history: ${exception.message}")
                    errorMessage.value = exception.message ?: "Gagal memuat riwayat"
                    isLoading.value = false
                }
        }
    }
    
    fun saveSearchQuery(query: String) {
        val token = authRepository.getToken() ?: return
        
        viewModelScope.launch {
            searchHistoryRepository.saveSearchHistory(token, query)
                .onSuccess {
                    // Reload history to get updated list (sorted by updated_at, so re-searched items appear at top)
                    loadSearchHistory()
                }
                .onFailure { exception ->
                    // Silent fail - tidak perlu tampilkan error saat save history
                    android.util.Log.e("SearchViewModel", "Failed to save search history: ${exception.message}")
                }
        }
    }
    
    fun deleteSearchHistory(id: Int) {
        val token = authRepository.getToken() ?: return
        
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchHistory(token, id)
                .onSuccess {
                    // Remove from local list
                    searchHistory.removeAll { it.id == id }
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal menghapus riwayat"
                }
        }
    }
    
    fun clearAllHistory() {
        val token = authRepository.getToken() ?: return
        
        isLoading.value = true
        
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory(token)
                .onSuccess {
                    searchHistory.clear()
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal menghapus semua riwayat"
                    isLoading.value = false
                }
        }
    }
}

