package com.example.projektbptb.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projektbptb.R
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.SearchViewModel

data class SearchCategory(
    val name: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSearchQuery: (String) -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    val searchHistory = viewModel.searchHistory
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    var isHistoryVisible by remember { mutableStateOf(true) }
    var showMoreHistory by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    
    // Refresh search history saat screen muncul
    LaunchedEffect(Unit) {
        viewModel.loadSearchHistory()
    }
    
    // Clear all history confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Hapus Semua Riwayat?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus semua riwayat pencarian?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    val categories = listOf(
        SearchCategory("Perabotan", R.drawable.perabotan),
        SearchCategory("Elektronik", R.drawable.elektronik),
        SearchCategory("Pakaian", R.drawable.pakaian),
        SearchCategory("Perlengkapan Kuliah", R.drawable.kuliah),
        SearchCategory("Sepatu", R.drawable.sepatu)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { newText ->
                            searchText = newText
                        },
                        placeholder = { Text("Find what you need...", color = GrayDark) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = GrayDark)
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = GrayDark)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
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
                                if (searchText.isNotBlank()) {
                                    // Save to search history via API
                                    viewModel.saveSearchQuery(searchText.trim())
                                    onSearchQuery(searchText.trim())
                                }
                            }
                        )
                    )
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Loading state
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BluePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            // Error message
            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = RedPrimary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = RedPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = error,
                                fontSize = 12.sp,
                                color = RedPrimary
                            )
                        }
                    }
                }
            }
            
            // History Section
            if (searchHistory.isNotEmpty() && isHistoryVisible && !isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Riwayat Pencarian",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                        }
                        TextButton(onClick = { showClearDialog = true }) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = RedPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Hapus Semua", color = RedPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // History Items - tampilkan semua jika showMoreHistory
                val displayedHistory = if (showMoreHistory) searchHistory else searchHistory.take(5)
                items(displayedHistory) { historyItem ->
                    HistoryItem(
                        text = historyItem.query,
                        onItemClick = {
                            searchText = historyItem.query
                            viewModel.saveSearchQuery(historyItem.query)
                            onSearchQuery(historyItem.query)
                        },
                        onDeleteClick = {
                            viewModel.deleteSearchHistory(historyItem.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Tombol "Lihat Lainnya" jika ada lebih dari 5 riwayat
                if (searchHistory.size > 5 && !showMoreHistory) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { showMoreHistory = true },
                            colors = CardDefaults.cardColors(containerColor = BlueLight),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Lihat Lainnya (${searchHistory.size - 5} lagi)",
                                    color = BluePrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                
                // Tombol "Sembunyikan" jika sudah expand
                if (showMoreHistory && searchHistory.size > 5) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { showMoreHistory = false },
                            colors = CardDefaults.cardColors(containerColor = GrayLight),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Sembunyikan",
                                    color = GrayDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = GrayDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            } else if (searchHistory.isEmpty() && !isLoading) {
                // Empty state for search history
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = GrayDark,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada riwayat pencarian",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cari produk untuk membuat riwayat",
                            fontSize = 14.sp,
                            color = GrayDark
                        )
                    }
                }
            }
            
            // Divider setelah riwayat (jika ada)
            if (searchHistory.isNotEmpty() && isHistoryVisible) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayLight)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Search by Category Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        Icons.Default.GridView,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Cari Berdasarkan Kategori",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                }
            }

            items(categories.chunked(2)) { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCategories.forEach { category ->
                        Box(modifier = Modifier.weight(1f)) {
                            CategoryCard(
                                category = category,
                        onClick = {
                            // Simpan juga kategori sebagai riwayat pencarian
                            viewModel.saveSearchQuery(category.name)
                            onCategoryClick(category.name)
                        }
                            )
                        }
                    }
                    // Fill remaining space if odd number
                    if (rowCategories.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun HistoryItem(
    text: String,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = GrayLight),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = text,
                    fontSize = 14.sp,
                    color = Black,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Tombol X untuk hapus
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(32.dp)
                    .background(White, CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Hapus riwayat",
                    tint = RedPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: SearchCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GrayLight)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay untuk readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}

