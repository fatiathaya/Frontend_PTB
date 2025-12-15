package com.example.projektbptb.data.repository

import com.example.projektbptb.data.model.Product
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
    
    suspend fun getProducts(category: String? = null, search: String? = null, token: String? = null): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.getProducts(token?.let { "Bearer $it" }, category, search)
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
        whatsappNumber: String?,
        imageFile: File?,
        token: String
    ): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                // Create multipart image part if file exists
                val imagePart: MultipartBody.Part? = imageFile?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                }
                
                // Create RequestBody for string fields
                val nameBody: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody: RequestBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                val conditionBody: RequestBody = (condition ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody: RequestBody? = description?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody: RequestBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
                val whatsappNumberBody: RequestBody = (whatsappNumber ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = productApiService.createProduct(
                    token = "Bearer $token",
                    name = nameBody,
                    category = categoryBody,
                    condition = conditionBody,
                    description = descriptionBody,
                    price = priceBody,
                    whatsappNumber = whatsappNumberBody,
                    image = imagePart
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
        whatsappNumber: String?,
        imageFile: File?,
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
                val priceBody = numericPrice.toRequestBody("text/plain".toMediaTypeOrNull())
                val whatsappBody = whatsappNumber?.toRequestBody("text/plain".toMediaTypeOrNull())
                
                // Create image part if file is provided
                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }
                
                val response = productApiService.updateProductMultipart(
                    token = "Bearer $token",
                    id = id,
                    method = "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
                    name = nameBody,
                    category = categoryBody,
                    condition = conditionBody,
                    description = descriptionBody,
                    price = priceBody,
                    whatsappNumber = whatsappBody,
                    image = imagePart
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toProduct())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to update product"))
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
    
    suspend fun toggleFavorite(id: Int, token: String): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productApiService.toggleFavorite("Bearer $token", id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toProduct())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to toggle favorite"))
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
                    Result.success(products)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get my products"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    private fun ProductResponse.toProduct(): Product {
        return Product(
            id = id.toString(),
            name = name,
            category = category,
            price = price,
            imageUrl = image_url, // Use image_url from API
            isFavorite = is_favorite,
            description = description,
            condition = condition,
            whatsappNumber = whatsapp_number,
            sellerName = seller_name ?: "Penjual"
        )
    }
}

