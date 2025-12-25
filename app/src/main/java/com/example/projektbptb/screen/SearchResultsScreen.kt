package com.example.projektbptb.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.ui.component.BottomNavigationBar
import com.example.projektbptb.ui.theme.*

enum class SortBy {
    TERBARU,
    HARGA_TERMURAH,
    HARGA_TERMAHAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchQuery: String = "",
    products: List<Product> = emptyList(),
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onFavoriteClick: (Product) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToSell: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    isProfileComplete: Boolean = true,
    onLoadProducts: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSaveSearchQuery: (String) -> Unit = {}
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf<SortBy?>(null) }
    var showNewestOnly by remember { mutableStateOf(false) }
    var currentSearchText by remember { mutableStateOf(searchQuery) }
    
    // Sinkronkan currentSearchText dengan searchQuery dari navigation
    LaunchedEffect(searchQuery) {
        currentSearchText = searchQuery
    }
    
    /**
     * Prevent "flash" of previous/all products when opening this screen:
     * - Mark the current query as pending immediately (in composition) so UI shows loading first.
     * - Only show results after we have observed loading=true at least once for this query, then loading=false.
     */
    var pendingQuery by remember(searchQuery) { mutableStateOf(searchQuery.isNotBlank()) }
    var sawLoadingForQuery by remember(searchQuery) { mutableStateOf(false) }
    var displayedProducts by remember(searchQuery) { mutableStateOf<List<Product>>(emptyList()) }
    val effectiveLoading = pendingQuery || isLoading
    
    // Trigger search saat pertama kali masuk screen atau saat query berubah
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            // Reset state untuk query baru
            pendingQuery = true
            sawLoadingForQuery = false
            displayedProducts = emptyList()
            // Simpan ke search history
            onSaveSearchQuery(searchQuery)
            // Load products
            onLoadProducts(searchQuery)
        }
    }

    LaunchedEffect(isLoading, products, searchQuery) {
        if (!pendingQuery) return@LaunchedEffect

        if (isLoading) {
            sawLoadingForQuery = true
        }

        // Only accept results after we saw loading=true once, then loading=false.
        if (sawLoadingForQuery && !isLoading) {
            displayedProducts = products
            pendingQuery = false
        }
    }

    // Sort produk (tidak perlu filter lagi karena sudah dari backend)
    val filteredProducts = remember(displayedProducts, effectiveLoading, sortBy, showNewestOnly) {
        var result = if (effectiveLoading) emptyList() else displayedProducts
        
        if (showNewestOnly) {
            // Simulasi produk terbaru (ambil 4 terakhir)
            result = result.takeLast(4)
        }
        
        when (sortBy) {
            SortBy.HARGA_TERMURAH -> {
                result.sortedBy { product ->
                    // Extract harga dari string "Rp 50.000"
                    product.price.replace("Rp", "")
                        .replace(".", "")
                        .replace(" ", "")
                        .toIntOrNull() ?: 0
                }
            }
            SortBy.HARGA_TERMAHAL -> {
                result.sortedByDescending { product ->
                    product.price.replace("Rp", "")
                        .replace(".", "")
                        .replace(" ", "")
                        .toIntOrNull() ?: 0
                }
            }
            SortBy.TERBARU -> result.reversed()
            null -> result
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = currentSearchText,
                            onValueChange = { currentSearchText = it },
                            placeholder = { Text("Cari...", color = GrayDark) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = GrayDark)
                            },
                            trailingIcon = {
                                if (currentSearchText.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        currentSearchText = ""
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = GrayDark)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = White,
                                unfocusedContainerColor = White,
                                focusedIndicatorColor = BluePrimary,
                                unfocusedIndicatorColor = GrayLight
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (currentSearchText.isNotBlank()) {
                                        val trimmedQuery = currentSearchText.trim()
                                        // Simpan ke search history
                                        onSaveSearchQuery(trimmedQuery)
                                        // Navigasi akan memicu LaunchedEffect yang memanggil onLoadProducts
                                        onSearchQueryChange(trimmedQuery)
                            }
                        }
                            )
                        )
                        TextButton(
                            onClick = { showFilterDialog = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = BluePrimary
                            )
                        ) {
                            // Icon filter sederhana menggunakan garis
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Transparent)
                            ) {
                                // Simulasi icon filter dengan garis
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp, 8.dp)
                                                .background(BluePrimary, RoundedCornerShape(1.dp))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp, 8.dp)
                                                .background(BluePrimary, RoundedCornerShape(1.dp))
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp, 8.dp)
                                                .background(BluePrimary, RoundedCornerShape(1.dp))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp, 8.dp)
                                                .background(BluePrimary, RoundedCornerShape(1.dp))
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filter", fontSize = 14.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "search",
                isProfileComplete = isProfileComplete,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSell = onNavigateToSell,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToCompleteProfile = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
        ) {
            // Header hasil pencarian
            Text(
                text = if (sortBy == SortBy.HARGA_TERMURAH) {
                    "Menampilkan harga termurah untuk \"$searchQuery\""
                } else if (sortBy == SortBy.HARGA_TERMAHAL) {
                    "Menampilkan harga termahal untuk \"$searchQuery\""
                } else {
                    "Hasil Pencarian \"$searchQuery\""
                },
                fontSize = 14.sp,
                color = GrayDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Product Grid
            when {
                effectiveLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = BluePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Mencari \"$searchQuery\"...",
                                fontSize = 14.sp,
                                color = GrayDark
                            )
                        }
                    }
                }
                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = GrayDark,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Tidak ada hasil untuk \"$searchQuery\"",
                                fontSize = 16.sp,
                                color = Black,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Coba kata kunci lain",
                                fontSize = 14.sp,
                                color = GrayDark
                            )
                        }
                    }
                }
                else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onFavoriteClick = { onFavoriteClick(product) },
                            onProductClick = { onProductClick(product) }
                        )
                    }
                }
                }
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            sortBy = sortBy,
            showNewestOnly = showNewestOnly,
            onSortByChange = { sortBy = it },
            onNewestOnlyChange = { showNewestOnly = it },
            onApply = {
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    sortBy: SortBy?,
    showNewestOnly: Boolean,
    onSortByChange: (SortBy?) -> Unit,
    onNewestOnlyChange: (Boolean) -> Unit,
    onApply: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = GrayDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content area (so dialog never overflows on small screens)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    // Produk Terbaru
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNewestOnlyChange(!showNewestOnly) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showNewestOnly,
                            onCheckedChange = onNewestOnlyChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = BluePrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Produk Terbaru",
                            fontSize = 16.sp,
                            color = Black
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Harga Section
                    Text(
                        text = "Harga",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Harga Termurah
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSortByChange(
                                    if (sortBy == SortBy.HARGA_TERMURAH) null else SortBy.HARGA_TERMURAH
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortBy == SortBy.HARGA_TERMURAH,
                            onClick = {
                                onSortByChange(
                                    if (sortBy == SortBy.HARGA_TERMURAH) null else SortBy.HARGA_TERMURAH
                                )
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BluePrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Harga Termurah",
                            fontSize = 16.sp,
                            color = Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Harga Termahal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSortByChange(
                                    if (sortBy == SortBy.HARGA_TERMAHAL) null else SortBy.HARGA_TERMAHAL
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortBy == SortBy.HARGA_TERMAHAL,
                            onClick = {
                                onSortByChange(
                                    if (sortBy == SortBy.HARGA_TERMAHAL) null else SortBy.HARGA_TERMAHAL
                                )
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BluePrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Harga Termahal",
                            fontSize = 16.sp,
                            color = Black
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Penilaian Section
                    Text(
                        text = "Penilaian",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    // TODO: Tambahkan opsi penilaian jika diperlukan

                    // Bottom padding so last items aren't hidden behind the button
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Apply Button
                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Terapkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }
            }
        }
    }
}


