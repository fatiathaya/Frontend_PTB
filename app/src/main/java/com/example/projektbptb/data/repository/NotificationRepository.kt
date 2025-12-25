package com.example.projektbptb.data.repository

import com.example.projektbptb.data.network.NotificationResponse
import com.example.projektbptb.data.network.NetworkModule
import com.example.projektbptb.data.network.UnreadCountResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository {
    private val notificationApiService = NetworkModule.notificationApiService
    
    suspend fun getNotifications(token: String): Result<List<NotificationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificationApiService.getNotifications("Bearer $token")
                android.util.Log.d("NotificationRepository", "Response code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    android.util.Log.d("NotificationRepository", "Response success: ${body?.success}, Data size: ${body?.data?.size ?: 0}")
                    
                    if (body?.success == true && body.data != null) {
                        android.util.Log.d("NotificationRepository", "Notifications loaded: ${body.data.size}")
                        Result.success(body.data)
                    } else {
                        val errorMsg = body?.message ?: "Failed to get notifications"
                        android.util.Log.e("NotificationRepository", "Error: $errorMsg")
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("NotificationRepository", "HTTP Error ${response.code()}: $errorBody")
                    Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                android.util.Log.e("NotificationRepository", "Exception: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun getUnreadCount(token: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificationApiService.getUnreadCount("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    // Backend returns { success: true, data: { count: X } }
                    val count = response.body()!!.data!!.count
                    Result.success(count)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get unread count"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun markAsRead(token: String, notificationId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificationApiService.markAsRead("Bearer $token", notificationId)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to mark notification as read"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun markAllAsRead(token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificationApiService.markAllAsRead("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to mark all notifications as read"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
