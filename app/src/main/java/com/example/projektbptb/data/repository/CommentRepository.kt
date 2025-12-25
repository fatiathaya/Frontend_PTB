package com.example.projektbptb.data.repository

import com.example.projektbptb.data.network.CommentResponse
import com.example.projektbptb.data.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentRepository {
    private val commentApiService = NetworkModule.commentApiService
    
    suspend fun getComments(productId: Int, token: String?): Result<List<CommentResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApiService.getComments(productId, token?.let { "Bearer $it" })
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get comments"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createComment(productId: Int, token: String, comment: String, parentCommentId: Int? = null): Result<CommentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("CommentRepository", "Creating comment: productId=$productId, comment=$comment, parentCommentId=$parentCommentId")
                val requestBody = mutableMapOf<String, String>("comment" to comment)
                if (parentCommentId != null) {
                    requestBody["parent_comment_id"] = parentCommentId.toString()
                }
                val response = commentApiService.createComment(productId, "Bearer $token", requestBody)
                android.util.Log.d("CommentRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    android.util.Log.d("CommentRepository", "Comment created successfully")
                    Result.success(response.body()!!.data!!)
                } else {
                    // Better error handling
                    val responseBody = response.body()
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    
                    android.util.Log.e("CommentRepository", "Error creating comment: code=${response.code()}, message=${responseBody?.message}, errorBody=$errorBody")
                    
                    // Check if error body is HTML (Laravel error page)
                    val isHtmlError = errorBody?.contains("<!DOCTYPE") == true || errorBody?.contains("<html") == true
                    
                    val errorMessage = when {
                        isHtmlError -> {
                            // If HTML error, check common causes
                            when {
                                errorBody?.contains("SQLSTATE") == true || 
                                errorBody?.contains("table") == true ||
                                errorBody?.contains("doesn't exist") == true ||
                                errorBody?.contains("Base table or view not found") == true -> 
                                    "Database error. Pastikan migration sudah dijalankan:\nphp artisan migrate"
                                errorBody?.contains("Class") == true && errorBody?.contains("not found") == true ->
                                    "Model atau class tidak ditemukan. Periksa konfigurasi backend."
                                errorBody?.contains("SQLSTATE[42S02]") == true ->
                                    "Tabel comments belum ada. Jalankan: php artisan migrate"
                                else -> "Server error. Pastikan:\n1. Database migration sudah dijalankan\n2. Server Laravel berjalan dengan benar"
                            }
                        }
                        responseBody?.errors != null -> {
                            // Format validation errors
                            responseBody.errors!!.entries.joinToString("\n") { (field, messages) ->
                                "$field: ${messages.joinToString(", ")}"
                            }
                        }
                        responseBody?.message != null -> responseBody.message!!
                        response.code() == 401 -> "Silakan login untuk berkomentar"
                        response.code() == 404 -> "Produk tidak ditemukan"
                        response.code() == 422 -> "Data tidak valid: ${errorBody ?: "Validation error"}"
                        response.code() == 500 -> "Server error. Pastikan database migration sudah dijalankan."
                        errorBody != null -> {
                            try {
                                // Try to parse JSON error if not HTML
                                if (!errorBody.contains("<!DOCTYPE") && !errorBody.contains("<html")) {
                                    errorBody
                                } else {
                                    "Server error. Periksa log backend."
                                }
                            } catch (e: Exception) {
                                "Gagal membuat komentar (Error ${response.code()})"
                            }
                        }
                        else -> "Gagal membuat komentar (Error ${response.code()})"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                android.util.Log.e("CommentRepository", "Exception creating comment", e)
                // Handle network errors
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Tidak dapat terhubung ke server. Pastikan server Laravel berjalan."
                    e.message?.contains("Connection refused") == true -> 
                        "Koneksi ditolak. Pastikan server Laravel berjalan."
                    e.message?.contains("timeout") == true -> 
                        "Koneksi timeout. Periksa koneksi internet Anda."
                    e.message?.contains("401") == true || e.message?.contains("Unauthenticated") == true ->
                        "Silakan login untuk berkomentar"
                    else -> e.message ?: "Gagal membuat komentar: ${e.javaClass.simpleName}"
                }
                Result.failure(Exception(errorMsg))
            }
        }
    }
    
    suspend fun updateComment(commentId: Int, token: String, comment: String): Result<CommentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApiService.updateComment(commentId, "Bearer $token", mapOf("comment" to comment))
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to update comment"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteComment(commentId: Int, token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApiService.deleteComment(commentId, "Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to delete comment"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

