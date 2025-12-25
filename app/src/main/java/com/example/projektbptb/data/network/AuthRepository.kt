package com.example.projektbptb.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.projektbptb.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AuthRepository(private val context: Context) {
    private val authApiService = NetworkModule.authApiService
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(LoginRequest(email, password))
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    val authResponse = responseBody.data!!
                    saveAuthData(authResponse)
                    Result.success(authResponse)
                } else {
                    // Get detailed error message
                    val errorMessage = when {
                        responseBody?.errors != null -> {
                            responseBody.errors.entries.joinToString("\n") { (field, messages) ->
                                "$field: ${messages.joinToString(", ")}"
                            }
                        }
                        responseBody?.message != null -> responseBody.message!!
                        response.errorBody() != null -> {
                            try {
                                response.errorBody()?.string() ?: "Login failed"
                            } catch (e: Exception) {
                                "Login failed: ${response.code()}"
                            }
                        }
                        else -> "Login failed: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> "Tidak dapat terhubung ke server. Pastikan server Laravel berjalan."
                    e.message?.contains("Connection refused") == true -> "Koneksi ditolak. Pastikan server Laravel berjalan di http://10.0.2.2:8000"
                    e.message?.contains("timeout") == true -> "Koneksi timeout. Periksa koneksi internet Anda."
                    else -> e.message ?: "Login failed: ${e.javaClass.simpleName}"
                }
                Result.failure(Exception(errorMsg))
            }
        }
    }
    
    suspend fun register(
        email: String,
        password: String,
        passwordConfirmation: String,
        name: String? = null,
        username: String? = null,
        phoneNumber: String? = null,
        gender: String? = null
    ): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.register(
                    RegisterRequest(name, email, password, passwordConfirmation, username, phoneNumber, gender)
                )
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    val authResponse = responseBody.data!!
                    // Disable auto-login after register
                    // saveAuthData(authResponse)
                    Result.success(authResponse)
                } else {
                    // Get detailed error message
                    val errorMessage = when {
                        responseBody?.errors != null -> {
                            // Format validation errors
                            responseBody.errors.entries.joinToString("\n") { (field, messages) ->
                                "$field: ${messages.joinToString(", ")}"
                            }
                        }
                        responseBody?.message != null -> responseBody.message!!
                        response.errorBody() != null -> {
                            try {
                                val errorJson = response.errorBody()?.string()
                                errorJson ?: "Registration failed"
                            } catch (e: Exception) {
                                "Registration failed: ${response.code()}"
                            }
                        }
                        else -> "Registration failed: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // Network or other errors
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> "Tidak dapat terhubung ke server. Pastikan server Laravel berjalan."
                    e.message?.contains("Connection refused") == true -> "Koneksi ditolak. Pastikan server Laravel berjalan di http://10.0.2.2:8000"
                    e.message?.contains("timeout") == true -> "Koneksi timeout. Periksa koneksi internet Anda."
                    else -> e.message ?: "Registration failed: ${e.javaClass.simpleName}"
                }
                Result.failure(Exception(errorMsg))
            }
        }
    }
    
    suspend fun getUser(): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(Exception("Not authenticated"))
                val response = authApiService.getUser("Bearer $token")
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    Result.success(responseBody.data!!)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to get user"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateUser(user: User): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(Exception("Not authenticated"))
                val updateData = mapOf(
                    "name" to user.name,
                    "username" to (user.username ?: ""),
                    "email" to user.email,
                    "phone_number" to (user.phoneNumber ?: ""),
                    "gender" to (user.gender ?: "")
                )
                val response = authApiService.updateUser("Bearer $token", updateData)
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    Result.success(responseBody.data!!)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to update user"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateUserWithImage(
        user: User,
        imageFile: File?
    ): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(Exception("Not authenticated"))
                
                // Create multipart image part if file exists
                val imagePart: MultipartBody.Part? = imageFile?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
                }
                
                // Create RequestBody for string fields
                val nameBody = user.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val usernameBody = (user.username ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = user.email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneBody = (user.phoneNumber ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val genderBody = (user.gender ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val methodBody = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = authApiService.updateUserWithImage(
                    token = "Bearer $token",
                    method = methodBody,
                    name = nameBody,
                    username = usernameBody,
                    email = emailBody,
                    phoneNumber = phoneBody,
                    gender = genderBody,
                    profileImage = imagePart
                )
                
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    Result.success(responseBody.data!!)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to update user"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteProfileImage(): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(Exception("Not authenticated"))
                val response = authApiService.deleteProfileImage("Bearer $token")
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    Result.success(responseBody.data!!)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to delete profile image"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken()
                if (token != null) {
                    authApiService.logout("Bearer $token")
                }
                clearAuthData()
                Result.success(Unit)
            } catch (e: Exception) {
                clearAuthData()
                Result.success(Unit) // Always clear local data even if API call fails
            }
        }
    }
    
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    
    suspend fun getUserProfile(userId: Int): Result<com.example.projektbptb.data.network.UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.getUserProfile(userId)
                val responseBody = response.body()

                if (response.isSuccessful && responseBody?.success == true && responseBody.data != null) {
                    Result.success(responseBody.data!!)
                } else {
                    val errorMessage = responseBody?.message
                        ?: response.errorBody()?.string()?.let {
                            try {
                                val json = org.json.JSONObject(it)
                                json.optString("message", "Failed to get user profile")
                            } catch (e: Exception) {
                                it
                            }
                        } ?: "Failed to get user profile"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun getUserId(): Int? {
        val userId = prefs.getInt(KEY_USER_ID, -1)
        return if (userId != -1) userId else null
    }
    
    fun isLoggedIn(): Boolean = getToken() != null
    
    suspend fun saveFcmToken(fcmToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(Exception("Not authenticated"))
                val response = authApiService.saveFcmToken("Bearer $token", mapOf("fcm_token" to fcmToken))
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Failed to save FCM token"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    private fun saveAuthData(authResponse: AuthResponse) {
        try {
            prefs.edit().apply {
                putString(KEY_TOKEN, authResponse.token)
                putInt(KEY_USER_ID, authResponse.user.id)
                putString(KEY_USER_NAME, authResponse.user.name)
                putString(KEY_USER_EMAIL, authResponse.user.email)
                apply()
            }
        } catch (e: Exception) {
            // Log error but don't crash
            Log.e("AuthRepository", "Error saving auth data: ${e.message}", e)
        }
    }
    
    private fun clearAuthData() {
        try {
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            // Log error but don't crash
            android.util.Log.e("AuthRepository", "Error clearing auth data: ${e.message}", e)
        }
    }
}

