package com.example.projektbptb.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    // TODO: Ganti dengan URL server Laravel Anda
    // Untuk emulator Android: gunakan 10.0.2.2 untuk localhost
    // Untuk device fisik: gunakan IP komputer Anda (misalnya: 192.168.1.100)
    // PENTING: Base URL harus diakhiri dengan /api/ untuk route API Laravel
    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    
    // Atau jika menggunakan device fisik, uncomment dan ganti dengan IP komputer Anda:
    // private const val BASE_URL = "http://192.168.1.100:8000/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val productApiService: ProductApiService = retrofit.create(ProductApiService::class.java)
    val addressApiService: AddressApiService = retrofit.create(AddressApiService::class.java)
}

