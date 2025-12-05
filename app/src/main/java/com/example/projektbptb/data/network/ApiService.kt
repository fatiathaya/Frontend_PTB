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
}

/**
 * API Service untuk Products
 */
interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") token: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
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
        @Part("location") location: RequestBody,
        @Part("price") price: RequestBody,
        @Part("whatsapp_number") whatsappNumber: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponse<ProductResponse>>
    
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body product: Map<String, Any>
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

