package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.repository.ProductRepository
import com.example.projektbptb.data.model.Product
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository()
    private val authRepository = AuthRepository(application)
    
    val categories = listOf("All", "Pakaian", "Perabotan", "Elektronik", "Perlengkapan Kuliah", "Sepatu")
    val selectedCategory = mutableStateOf("All")
    
    val products = mutableStateListOf<Product>()
    // Dedicated list for search results to prevent flashing the home product list.
    val searchResults = mutableStateListOf<Product>()
    val favoriteProducts = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    val isSearching = mutableStateOf(false)
    val isLoadingFavorites = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val showAlert = mutableStateOf(false)
    val alertMessage = mutableStateOf<String?>(null)
    val currentUser = mutableStateOf<com.example.projektbptb.data.model.User?>(null)
    
    init {
        loadProducts()
        loadFavorites()
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getUser()
                .onSuccess { userResponse ->
                    currentUser.value = com.example.projektbptb.data.model.User(
                        id = userResponse.id,
                        name = userResponse.name,
                        username = userResponse.username,
                        email = userResponse.email,
                        phoneNumber = userResponse.phone_number,
                        gender = userResponse.gender,
                        profileImageUrl = userResponse.profile_image
                    )
                }
        }
    }
    
    fun isProfileComplete(): Boolean {
        return currentUser.value?.let { user ->
            !user.email.isNullOrBlank() && !user.phoneNumber.isNullOrBlank()
        } ?: false
    }
    
    fun loadProducts(category: String? = null, search: String? = null) {
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            val token = authRepository.getToken()
            val categoryFilter = if (category == "All" || category == null) null else category
            
            productRepository.getProducts(category = categoryFilter, search = search, token = token)
                .onSuccess { productList ->
                    products.clear()
                    products.addAll(productList)
                    
                    // Setelah load products, sync status isFavorite dengan favoriteProducts
                    // Ini memastikan status wishlist tetap konsisten meskipun products di-reload
                    syncFavoriteStatus()
                    
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memuat produk"
                    isLoading.value = false
                }
        }
    }

    /**
     * Search products by query (name/category) without mutating the home product list.
     * This avoids a brief UI flash of the previous/all products while search is loading.
     */
    fun searchProducts(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        isSearching.value = true
        errorMessage.value = null
        // Clear immediately so UI never shows stale results.
        searchResults.clear()

        viewModelScope.launch {
            val token = authRepository.getToken()
            productRepository.getProducts(category = null, search = trimmed, token = token)
                .onSuccess { productList ->
                    searchResults.clear()
                    searchResults.addAll(productList)

                    // Sync favorite status for search results too.
                    if (favoriteProducts.isNotEmpty()) {
                        val favoriteIds = favoriteProducts.map { it.id }.toSet()
                        val updates = mutableListOf<Pair<Int, Product>>()
                        searchResults.forEachIndexed { index, product ->
                            val shouldBeFavorite = favoriteIds.contains(product.id)
                            if (product.isFavorite != shouldBeFavorite) {
                                updates.add(index to product.copy(isFavorite = shouldBeFavorite))
                            }
                        }
                        updates.sortedByDescending { it.first }.forEach { (index, updated) ->
                            searchResults.removeAt(index)
                            searchResults.add(index, updated)
                        }
                    }

                    isSearching.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal mencari produk"
                    isSearching.value = false
                }
        }
    }
    
    /**
     * Sync status isFavorite di products list dengan favoriteProducts
     * Ini memastikan status wishlist tetap konsisten setelah loadProducts atau loadFavorites
     */
    private fun syncFavoriteStatus() {
        // Jika favoriteProducts kosong, set semua isFavorite = false
        if (favoriteProducts.isEmpty()) {
            val productsToUpdate = mutableListOf<Pair<Int, Product>>()
            products.forEachIndexed { index, product ->
                if (product.isFavorite) {
                    productsToUpdate.add(index to product.copy(isFavorite = false))
                }
            }
            // Update dalam reverse order untuk menjaga index tetap benar
            productsToUpdate.sortedByDescending { it.first }.forEach { (index, updatedProduct) ->
                products.removeAt(index)
                products.add(index, updatedProduct)
            }
            return
        }
        
        // Sync berdasarkan favoriteProducts yang ada
        val favoriteIds = favoriteProducts.map { it.id }.toSet()
        val productsToUpdate = mutableListOf<Pair<Int, Product>>()
        
        products.forEachIndexed { index, product ->
            val shouldBeFavorite = favoriteIds.contains(product.id)
            if (product.isFavorite != shouldBeFavorite) {
                productsToUpdate.add(index to product.copy(isFavorite = shouldBeFavorite))
            }
        }
        
        // Update dalam reverse order untuk menjaga index tetap benar
        productsToUpdate.sortedByDescending { it.first }.forEach { (index, updatedProduct) ->
            products.removeAt(index)
            products.add(index, updatedProduct)
        }
    }
    
    fun toggleFavorite(product: Product) {
        val token = authRepository.getToken()
        if (token == null) {
            errorMessage.value = "Silakan login terlebih dahulu"
            return
        }
        
        viewModelScope.launch {
            productRepository.toggleFavorite(product.id.toIntOrNull() ?: 0, token)
                .onSuccess { updatedProduct ->
                    // Update product in products list dengan status favorite yang benar dari server
                    // Gunakan cara yang trigger re-composition dengan benar
                    val index = products.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        // Remove dan add kembali untuk memastikan Compose detect perubahan
                        products.removeAt(index)
                        products.add(index, updatedProduct)
                    }
                    
                    // Update favorite products list berdasarkan status terbaru dari server
                    // Status wishlist harus konsisten: jika isFavorite = true, ada di list
                    // jika isFavorite = false, tidak ada di list
                    if (updatedProduct.isFavorite) {
                        // Jika sekarang favorite, tambahkan ke list jika belum ada
                        if (favoriteProducts.none { it.id == updatedProduct.id }) {
                            favoriteProducts.add(updatedProduct)
                        } else {
                            // Update jika sudah ada untuk memastikan data terbaru
                            val favIndex = favoriteProducts.indexOfFirst { it.id == updatedProduct.id }
                            if (favIndex != -1) {
                                favoriteProducts.removeAt(favIndex)
                                favoriteProducts.add(favIndex, updatedProduct)
                            }
                        }
                    } else {
                        // Jika sekarang tidak favorite, hapus dari list
                        // Ini memastikan produk yang tidak favorite tidak muncul di wishlist
                        favoriteProducts.removeAll { it.id == updatedProduct.id }
                    }
                    
                    // Reload favorites from API untuk memastikan data selalu sinkron dengan server
                    // Ini penting untuk memastikan status wishlist tetap konsisten di semua screen
                    // Setelah loadFavorites selesai, products list akan ter-sync otomatis
                    loadFavorites()
                }
                .onFailure { exception ->
                    val message = exception.message ?: "Gagal mengubah favorite"
                    errorMessage.value = message
                    
                    // Tampilkan alert khusus jika user mencoba wishlist produk sendiri
                    if (message.contains("tidak bisa", ignoreCase = true) || 
                        message.contains("produk sendiri", ignoreCase = true)) {
                        alertMessage.value = "Tidak bisa wishlist produk sendiri"
                        showAlert.value = true
                    }
                }
        }
    }
    
    fun loadFavorites() {
        val token = authRepository.getToken()
        if (token == null) {
            favoriteProducts.clear()
            // Update products list untuk set semua isFavorite = false
            // Gunakan removeAt dan add untuk memastikan Compose detect perubahan
            val productsToUpdate = mutableListOf<Pair<Int, Product>>()
            products.forEachIndexed { index, product ->
                if (product.isFavorite) {
                    productsToUpdate.add(index to product.copy(isFavorite = false))
                }
            }
            // Update dalam reverse order untuk menjaga index tetap benar
            productsToUpdate.sortedByDescending { it.first }.forEach { (index, updatedProduct) ->
                products.removeAt(index)
                products.add(index, updatedProduct)
            }
            return
        }
        
        isLoadingFavorites.value = true
        viewModelScope.launch {
            productRepository.getFavorites(token)
                .onSuccess { favoriteList ->
                    favoriteProducts.clear()
                    favoriteProducts.addAll(favoriteList)
                    
                    // Sync status isFavorite di products list dengan favoriteProducts
                    syncFavoriteStatus()
                    
                    isLoadingFavorites.value = false
                    errorMessage.value = null
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memuat wishlist"
                    isLoadingFavorites.value = false
                    // Clear list on error to show empty state
                    favoriteProducts.clear()
                    // Update products list untuk set semua isFavorite = false
                    // Gunakan removeAt dan add untuk memastikan Compose detect perubahan
                    val productsToUpdate = mutableListOf<Pair<Int, Product>>()
                    products.forEachIndexed { index, product ->
                        if (product.isFavorite) {
                            productsToUpdate.add(index to product.copy(isFavorite = false))
                        }
                    }
                    // Update dalam reverse order untuk menjaga index tetap benar
                    productsToUpdate.sortedByDescending { it.first }.forEach { (index, updatedProduct) ->
                        products.removeAt(index)
                        products.add(index, updatedProduct)
                    }
                }
        }
    }
    
    fun selectCategory(category: String) {
        selectedCategory.value = category
        loadProducts(category)
        // loadFavorites akan dipanggil setelah loadProducts selesai melalui syncFavoriteStatus
    }
    
    fun removeFavorite(product: Product) {
        toggleFavorite(product)
    }
    
    fun refresh() {
        loadProducts(selectedCategory.value)
        loadFavorites()
    }
}
