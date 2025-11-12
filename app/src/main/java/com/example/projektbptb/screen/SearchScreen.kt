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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.projektbptb.R
import com.example.projektbptb.ui.theme.*

data class SearchCategory(
    val name: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onSearchQuery: (String) -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var searchHistory by remember { mutableStateOf(listOf("Baju", "Meja", "Kursi", "Celana")) }
    var isHistoryVisible by remember { mutableStateOf(true) }
    var showMoreHistory by remember { mutableStateOf(false) }

    val categories = listOf(
        SearchCategory("Perabotan", R.drawable.teddy),
        SearchCategory("Elektronik", R.drawable.teddy),
        SearchCategory("Kasur", R.drawable.teddy),
        SearchCategory("Pakaian", R.drawable.dress),
        SearchCategory("Barang Kuliah", R.drawable.toy),
        SearchCategory("Sepatu", R.drawable.shoes)
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
                                    // Tambahkan ke history jika belum ada
                                    if (!searchHistory.contains(searchText.trim())) {
                                        searchHistory = listOf(searchText.trim()) + searchHistory.take(9)
                                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // History Section
            if (searchHistory.isNotEmpty() && isHistoryVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    TextButton(onClick = { isHistoryVisible = false }) {
                        Text("Hide", color = BluePrimary, fontSize = 14.sp)
                    }
                }

                // History Items
                val displayedHistory = if (showMoreHistory) searchHistory else searchHistory.take(4)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(displayedHistory) { historyItem ->
                        HistoryItem(
                            text = historyItem,
                            onItemClick = {
                                searchText = historyItem
                                onSearchQuery(historyItem)
                            },
                            onDeleteClick = {
                                searchHistory = searchHistory.filter { it != historyItem }
                            }
                        )
                    }
                    
                    if (searchHistory.size > 4 && !showMoreHistory) {
                        item {
                            TextButton(
                                onClick = { showMoreHistory = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("More", color = BluePrimary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search by Category Section
            Text(
                text = "Search by category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.name) }
                    )
                }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon history sederhana menggunakan Box dengan background
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(GrayDark.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "H",
                    fontSize = 10.sp,
                    color = GrayDark,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = text,
                fontSize = 14.sp,
                color = Black
            )
        }
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Delete",
                tint = BluePrimary,
                modifier = Modifier.size(18.dp)
            )
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

