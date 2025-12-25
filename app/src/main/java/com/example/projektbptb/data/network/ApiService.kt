package com.example.projektbptb.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service untuk Authentication
 */
interface AuthApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Unit>>
    
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<ApiResponse<UserResponse>>
    
    @PUT("user")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: Map<String, String>
    ): Response<ApiResponse<UserResponse>>
    
    @Multipart
    @POST("user")
    suspend fun updateUserWithImage(
        @Header("Authorization") token: String,
        @Part("_method") method: RequestBody,
        @Part("name") name: RequestBody?,
        @Part("username") username: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone_number") phoneNumber: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<UserResponse>>
    
    @DELETE("user/profile-image")
    suspend fun deleteProfileImage(@Header("Authorization") token: String): Response<ApiResponse<UserResponse>>
    
    @POST("user/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Unit>>
    
    @POST("user/fcm-token")
    suspend fun saveFcmToken(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Unit>>
    
    @GET("users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: Int
    ): Response<ApiResponse<UserProfileResponse>>
}

/**
 * API Service untuk Products
 */
interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") token: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("user_id") userId: Int? = null
    ): Response<ApiResponse<List<ProductResponse>>>
    
    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: Int,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<ProductResponse>>
    
    @Multipart
    @POST("products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("price") price: RequestBody,
        @Part("whatsapp_number") whatsappNumber: RequestBody,
        @Part image: MultipartBody.Part?,
        // New multi-image support (preferred)
        @Part images: Array<MultipartBody.Part>? = null
    ): Response<ApiResponse<ProductResponse>>
    
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body product: Map<String, Any>
    ): Response<ApiResponse<ProductResponse>>
    
    @Multipart
    @POST("products/{id}")
    suspend fun updateProductMultipart(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("_method") method: RequestBody,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("condition") condition: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("price") price: RequestBody,
        @Part("whatsapp_number") whatsappNumber: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part images: Array<MultipartBody.Part>?,
        @Part deleteImageIds: Array<MultipartBody.Part>?
    ): Response<ApiResponse<ProductResponse>>
    
    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>
    
    @POST("products/{id}/favorite")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<ProductResponse>>
    
    @GET("products/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<ProductResponse>>>
    
    @GET("products/my-products")
    suspend fun getMyProducts(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<ProductResponse>>>
    
    @DELETE("products/{productId}/images/{imageId}")
    suspend fun deleteProductImage(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int,
        @Path("imageId") imageId: Int
    ): Response<ApiResponse<Map<String, Any>>>
}

/**
 * API Service untuk Addresses
 */
interface AddressApiService {
    @GET("addresses")
    suspend fun getAddresses(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<AddressResponse>>>
    
    @GET("addresses/{id}")
    suspend fun getAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<AddressResponse>>
    
    @POST("addresses")
    suspend fun createAddress(
        @Header("Authorization") token: String,
        @Body address: Map<String, String>
    ): Response<ApiResponse<AddressResponse>>
    
    @PUT("addresses/{id}")
    suspend fun updateAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body address: Map<String, String>
    ): Response<ApiResponse<AddressResponse>>
    
    @DELETE("addresses/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>
}

/**
 * API Service untuk Search History
 */
interface SearchHistoryApiService {
    @GET("search-history")
    suspend fun getSearchHistory(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<SearchHistoryResponse>>>
    
    @POST("search-history")
    suspend fun saveSearchHistory(
        @Header("Authorization") token: String,
        @Body query: Map<String, String>
    ): Response<ApiResponse<SearchHistoryResponse>>
    
    @DELETE("search-history/{id}")
    suspend fun deleteSearchHistory(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>
    
    @HTTP(method = "DELETE", path = "search-history", hasBody = false)
    suspend fun clearSearchHistory(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
}

/**
 * API Service untuk Comments
 */
interface CommentApiService {
    @GET("products/{productId}/comments")
    suspend fun getComments(
        @Path("productId") productId: Int,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<List<CommentResponse>>>
    
    @POST("products/{productId}/comments")
    suspend fun createComment(
        @Path("productId") productId: Int,
        @Header("Authorization") token: String,
        @Body comment: Map<String, String> // Can include "comment" and optional "parent_comment_id"
    ): Response<ApiResponse<CommentResponse>>
    
    @PUT("comments/{id}")
    suspend fun updateComment(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body comment: Map<String, String>
    ): Response<ApiResponse<CommentResponse>>
    
    @DELETE("comments/{id}")
    suspend fun deleteComment(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
}

/**
 * API Service untuk Notifications
 */
interface NotificationApiService {
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<NotificationResponse>>>
    
    @GET("notifications/unread-count")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UnreadCountResponse>>
    
    @PUT("notifications/{id}/read")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>
    
    @PUT("notifications/read-all")
    suspend fun markAllAsRead(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
}

