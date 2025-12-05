package com.example.projektbptb.data.network

/**
 * Generic API Response wrapper untuk Laravel API
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: Map<String, List<String>>? = null
)

/**
 * Response untuk Login/Register
 */
data class AuthResponse(
    val user: UserResponse,
    val token: String
)

/**
 * User Response dari API
 */
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val username: String? = null,
    val phone_number: String? = null,
    val gender: String? = null,
    val profile_image: String? = null
)

/**
 * Product Response dari API
 */
data class ProductResponse(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val description: String? = null,
    val condition: String? = null,
    val location: String? = null,
    val whatsapp_number: String? = null,
    val image_url: String? = null,
    val user_id: Int? = null,
    val seller_name: String? = null,
    val is_favorite: Boolean = false,
    val created_at: String? = null,
    val updated_at: String? = null
)

/**
 * Address Response dari API
 */
data class AddressResponse(
    val id: Int,
    val label: String,
    val full_address: String,
    val location_name: String? = null,
    val detail_location: String? = null,
    val landmark: String? = null,
    val user_id: Int? = null
)

/**
 * Login Request
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Register Request
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val username: String? = null,
    val phone_number: String? = null,
    val gender: String? = null
)

