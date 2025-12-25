package com.example.projektbptb.data.repository

import com.example.projektbptb.data.model.Product
import com.example.projektbptb.data.model.ProductImage
import com.example.projektbptb.data.network.NetworkModule
import com.example.projektbptb.data.network.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductRepository {
    private val productApiService = NetworkModule.productApiService
    
    suspend fun getProducts(category: String? = null, search: String? = null, userId: Int? = null, token: String? = null): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.getProducts(token?.let { "Bearer $it" }, category, search, userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val products = response.body()!!.data!!.map { it.toProduct() }
                    Result.success(products)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get products"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getProduct(id: Int, token: String? = null): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.getProduct(id, token?.let { "Bearer $it" })
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toProduct())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get product"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createProduct(
        name: String,
        category: String,
        price: String,
        description: String?,
        condition: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?,
        whatsappNumber: String?,
        imageFile: File?,
        imageFiles: List<File>? = null,
        token: String
    ): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                // Legacy single image part (optional)
                val imagePart: MultipartBody.Part? = imageFile?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                // Multi-image parts (preferred) as Array
                val imageParts: Array<MultipartBody.Part>? = imageFiles?.mapIndexed { index, file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    // Laravel expects images[] array
                    MultipartBody.Part.createFormData("images[]", file.name.ifBlank { "image_$index.jpg" }, requestFile)
                }?.toTypedArray()
                
                // Create RequestBody for string fields
                val nameBody: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody: RequestBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                val conditionBody: RequestBody = (condition ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody: RequestBody? = description?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val addressBody: RequestBody? = address?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val latitudeBody: RequestBody? = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val longitudeBody: RequestBody? = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody: RequestBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
                val whatsappNumberBody: RequestBody = (whatsappNumber ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = productApiService.createProduct(
                    token = "Bearer $token",
                    name = nameBody,
                    category = categoryBody,
                    condition = conditionBody,
                    description = descriptionBody,
                    address = addressBody,
                    latitude = latitudeBody,
                    longitude = longitudeBody,
                    price = priceBody,
                    whatsappNumber = whatsappNumberBody,
                    image = imagePart,
                    images = imageParts
                )
                
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!.toProduct())
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to create product"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateProduct(
        id: Int,
        name: String,
        category: String,
        price: String,
        description: String?,
        condition: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?,
        whatsappNumber: String?,
        imageFile: File?,
        imageFiles: List<File>?,
        deleteImageIds: List<Int>?,
        token: String
    ): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                // Strip "Rp " and dots from price to get numeric value
                val numericPrice = price.replace("Rp ", "").replace(".", "").trim()
                
                // Create multipart request body parts
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                val conditionBody = condition?.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
                val addressBody = address?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val latitudeBody = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val longitudeBody = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody = numericPrice.toRequestBody("text/plain".toMediaTypeOrNull())
                val whatsappBody = whatsappNumber?.toRequestBody("text/plain".toMediaTypeOrNull())
                
                // Create image part if file is provided (legacy single)
                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }
                
                // Create multiple image parts as Array
                val imageParts = imageFiles?.mapIndexed { index, file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images[$index]", file.name, requestFile)
                }?.toTypedArray()
                
                // Create delete image IDs as MultipartBody.Part array
                // CRITICAL: Use index-based keys to avoid Laravel treating them as files
                // Laravel expects delete_image_ids[0], delete_image_ids[1], etc. for arrays
                val deleteImageIdParts = deleteImageIds?.mapIndexed { index, id ->
                    val requestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    // Use indexed key format: delete_image_ids[0], delete_image_ids[1], etc.
                    MultipartBody.Part.createFormData("delete_image_ids[$index]", id.toString(), requestBody)
                }?.toTypedArray()
                
                val response = productApiService.updateProductMultipart(
                    token = "Bearer $token",
                    id = id,
                    method = "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
                    name = nameBody,
                    category = categoryBody,
                    condition = conditionBody,
                    description = descriptionBody,
                    address = addressBody,
                    latitude = latitudeBody,
                    longitude = longitudeBody,
                    price = priceBody,
                    whatsappNumber = whatsappBody,
                    image = imagePart,
                    images = imageParts,
                    deleteImageIds = deleteImageIdParts
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedProduct = response.body()!!.data!!.toProduct()
                    android.util.Log.d("ProductRepository", "Product updated successfully: " +
                            "id=${updatedProduct.id}, " +
                            "images=${updatedProduct.images?.size ?: 0}, " +
                            "image IDs=${updatedProduct.images?.map { it.id }}")
                    Result.success(updatedProduct)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = response.body()?.message 
                        ?: errorBody 
                        ?: "Failed to update product (${response.code()})"
                    android.util.Log.e("ProductRepository", "Update product failed: $errorMessage")
                    android.util.Log.e("ProductRepository", "Error body: $errorBody")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteProduct(id: Int, token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.deleteProduct("Bearer $token", id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to delete product"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteProductImage(productId: Int, imageId: Int, token: String): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.deleteProductImage("Bearer $token", productId, imageId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data!!
                    val images = (data["images"] as? List<Map<String, Any>>)?.map { img ->
                        ProductImage(
                            id = (img["id"] as? Number)?.toInt() ?: 0,
                            url = img["url"] as? String ?: ""
                        )
                    } ?: emptyList()
                    
                    // Create updated product with new images
                    val updatedProduct = Product(
                        id = (data["product_id"] as? Number)?.toInt()?.toString() ?: "",
                        name = "", // Will be updated from existing product
                        category = "",
                        price = "",
                        imageUrl = images.firstOrNull()?.url,
                        images = images,
                        description = null,
                        condition = null,
                        address = null,
                        latitude = null,
                        longitude = null,
                        whatsappNumber = null,
                        sellerName = null,
                        isFavorite = false
                    )
                    Result.success(updatedProduct)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = response.body()?.message
                        ?: errorBody
                        ?: "Failed to delete image"
                    android.util.Log.e("ProductRepository", "Delete image failed: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                android.util.Log.e("ProductRepository", "Delete image error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun toggleFavorite(id: Int, token: String): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.toggleFavorite("Bearer $token", id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toProduct())
                } else {
                    // Coba baca error message dari response body atau error body
                    val errorMessage = response.body()?.message 
                        ?: response.errorBody()?.string()?.let { 
                            // Coba parse JSON error jika ada
                            try {
                                val json = org.json.JSONObject(it)
                                json.optString("message", "Failed to toggle favorite")
                            } catch (e: Exception) {
                                it
                            }
                        } ?: "Failed to toggle favorite"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getFavorites(token: String): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.getFavorites("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        val products = body.data.map { it.toProduct() }
                        Result.success(products)
                    } else {
                        // If no favorites, return empty list
                        Result.success(emptyList())
                    }
                } else {
                    val errorMessage = response.body()?.message ?: response.message() ?: "Failed to get favorites"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getMyProducts(token: String): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.getMyProducts("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    val products = response.body()!!.data!!.map { it.toProduct() }
                    android.util.Log.d("ProductRepository", "getMyProducts: loaded ${products.size} products")
                    products.forEach { product ->
                        android.util.Log.d("ProductRepository", "Product: id=${product.id}, name=${product.name}, images=${product.images?.size ?: 0}, image IDs=${product.images?.map { it.id }}")
                    }
                    Result.success(products)
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to get my products"
                    android.util.Log.e("ProductRepository", "getMyProducts failed: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                android.util.Log.e("ProductRepository", "getMyProducts exception: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    fun ProductResponse.toProduct(): Product {
        // Parse images array (preferred) or fallback to image_urls (legacy) or image_url (single)
        val productImages = when {
            !images.isNullOrEmpty() -> images.map { ProductImage(it.id, it.url) }
            !image_urls.isNullOrEmpty() -> image_urls.mapIndexed { index, url -> ProductImage(index, url) }
            !image_url.isNullOrEmpty() -> listOf(ProductImage(0, image_url))
            else -> emptyList()
        }
        
        return Product(
            id = id.toString(),
            name = name,
            category = category,
            price = price,
            imageUrl = image_url, // Keep for backward compatibility
            images = productImages,
            isFavorite = is_favorite,
            description = description,
            condition = condition,
            address = address,
            latitude = latitude,
            longitude = longitude,
            location = location, // Location field from database
            whatsappNumber = whatsapp_number,
            sellerName = seller_name ?: "Penjual",
            sellerUsername = seller_username,
            sellerProfileImage = seller_profile_image,
            userId = user_id,
            isOwnProduct = is_own_product
        )
    }
}

