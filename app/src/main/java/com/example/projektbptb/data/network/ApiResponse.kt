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
data class ProductImageResponse(
    val id: Int,
    val url: String
)

data class ProductResponse(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val description: String? = null,
    val condition: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val location: String? = null, // Location field from database
    val whatsapp_number: String? = null,
    val image_url: String? = null,
    val images: List<ProductImageResponse>? = null, // Multi-image support
    val image_urls: List<String>? = null, // Legacy support
    val user_id: Int? = null,
    val seller_name: String? = null,
    val seller_username: String? = null,
    val seller_profile_image: String? = null,
    val is_favorite: Boolean = false,
    val is_own_product: Boolean = false, // Flag untuk menandai produk milik user sendiri
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
    val name: String? = null,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val username: String? = null,
    val phone_number: String? = null,
    val gender: String? = null
)

/**
 * Search History Response
 */
data class SearchHistoryResponse(
    val id: Int,
    val query: String,
    val created_at: String
)

/**
 * Comment Response dari API
 */
data class CommentResponse(
    val id: Int,
    val product_id: Int,
    val user_id: Int,
    val user_name: String,
    val user_username: String? = null,
    val user_profile_image: String? = null,
    val comment: String,
    val parent_comment_id: Int? = null,
    val replies: List<CommentResponse>? = null,
    val created_at: String,
    val updated_at: String
)

/**
 * Change Password Request
 */
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)

/**
 * Notification Response dari API
 */
data class NotificationResponse(
    val id: Int,
    val type: String,
    val title: String,
    val message: String,
    val is_read: Boolean,
    val product_id: Int,
    val product_name: String? = null,
    val product_image: String? = null,
    val comment_id: Int? = null,
    val comment_text: String? = null,
    val created_at: String,
    val timestamp: String? = null
)

/**
 * Unread Count Response
 */
data class UnreadCountResponse(
    val count: Int
)

/**
 * User Profile Response (for viewing other users' profiles)
 */
data class UserProfileResponse(
    val id: Int,
    val name: String,
    val username: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val profile_image: String? = null,
    val joined_at: String? = null
)

