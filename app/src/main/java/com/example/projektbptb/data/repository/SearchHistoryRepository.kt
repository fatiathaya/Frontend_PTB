package com.example.projektbptb.data.repository

import com.example.projektbptb.data.network.NetworkModule
import com.example.projektbptb.data.network.SearchHistoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryRepository {
    private val searchHistoryApiService = NetworkModule.searchHistoryApiService
    
    suspend fun getSearchHistory(token: String): Result<List<SearchHistoryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = searchHistoryApiService.getSearchHistory("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data ?: emptyList())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get search history"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun saveSearchHistory(token: String, query: String): Result<SearchHistoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = searchHistoryApiService.saveSearchHistory(
                    "Bearer $token",
                    mapOf("query" to query)
                )
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to save search history"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteSearchHistory(token: String, id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = searchHistoryApiService.deleteSearchHistory("Bearer $token", id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to delete search history"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun clearSearchHistory(token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = searchHistoryApiService.clearSearchHistory("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to clear search history"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

