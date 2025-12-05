package com.example.projektbptb.data.repository

import com.example.projektbptb.data.model.Address
import com.example.projektbptb.data.network.AddressResponse
import com.example.projektbptb.data.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressRepository {
    private val addressApiService = NetworkModule.addressApiService
    
    suspend fun getAddresses(token: String): Result<List<Address>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = addressApiService.getAddresses("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    val addresses = response.body()!!.data!!.map { it.toAddress() }
                    Result.success(addresses)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get addresses"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getAddress(id: Int, token: String): Result<Address> {
        return withContext(Dispatchers.IO) {
            try {
                val response = addressApiService.getAddress("Bearer $token", id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toAddress())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get address"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createAddress(
        label: String,
        fullAddress: String,
        locationName: String?,
        detailLocation: String?,
        landmark: String?,
        token: String
    ): Result<Address> {
        return withContext(Dispatchers.IO) {
            try {
                val addressData = mutableMapOf<String, String>(
                    "label" to label,
                    "full_address" to fullAddress
                )
                locationName?.let { addressData["location_name"] = it }
                detailLocation?.let { addressData["detail_location"] = it }
                landmark?.let { addressData["landmark"] = it }
                
                val response = addressApiService.createAddress("Bearer $token", addressData)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toAddress())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to create address"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateAddress(
        id: Int,
        label: String,
        fullAddress: String,
        locationName: String?,
        detailLocation: String?,
        landmark: String?,
        token: String
    ): Result<Address> {
        return withContext(Dispatchers.IO) {
            try {
                val addressData = mutableMapOf<String, String>(
                    "label" to label,
                    "full_address" to fullAddress
                )
                locationName?.let { addressData["location_name"] = it }
                detailLocation?.let { addressData["detail_location"] = it }
                landmark?.let { addressData["landmark"] = it }
                
                val response = addressApiService.updateAddress("Bearer $token", id, addressData)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.data!!.toAddress())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to update address"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteAddress(id: Int, token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = addressApiService.deleteAddress("Bearer $token", id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to delete address"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    private fun AddressResponse.toAddress(): Address {
        return Address(
            id = id.toString(),
            label = label,
            fullAddress = full_address,
            locationName = location_name ?: "",
            detailLocation = detail_location ?: "",
            landmark = landmark ?: ""
        )
    }
}

