package com.example.projektbptb.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: Int,
    onBackClick: () -> Unit = {},
    onNavigateToProductDetail: (Int) -> Unit = {},
    viewModel: UserProfileViewModel
) {
    val user by viewModel.user.collectAsState(initial = null)
    val products by viewModel.products.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil Penjual",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GrayLight.copy(alpha = 0.3f))
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = RedPrimary,
                            fontSize = 14.sp
                        )
                        Button(
                            onClick = {
                                viewModel.clearError()
                                if (userId > 0) {
                                    viewModel.loadUserProfile(userId)
                                }
                            }
                        ) {
                            Text("Coba Lagi")
                        }
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GrayDark
                            )
                        ) {
                            Text("Kembali")
                        }
                    }
                }
                user != null -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
                    ) {
                        // Header with Profile Info
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Profile Picture
                                    if (!user?.profileImageUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = user?.profileImageUrl,
                                            contentDescription = "Foto Profil",
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .background(BlueLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = user?.name?.firstOrNull()?.toString()?.uppercase() ?: "P",
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BluePrimary
                                            )
                                        }
                                    }

                                    // Name
                                    Text(
                                        text = user?.name ?: "Penjual",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Black
                                    )

                                    // Products Count
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${products.size}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BluePrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Produk",
                                            fontSize = 16.sp,
                                            color = GrayDark
                                        )
                                    }

                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = GrayLight
                                    )

                                    // Email (Clickable)
                                    if (!user?.email.isNullOrEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    user?.email?.let { email ->
                                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                            data = Uri.parse("mailto:$email")
                                                        }
                                                        context.startActivity(intent)
                                                    }
                                                }
                                                .background(BlueLight.copy(alpha = 0.3f))
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Email,
                                                contentDescription = "Email",
                                                tint = BluePrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = user?.email ?: "",
                                                fontSize = 14.sp,
                                                color = Black,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }

                                    // WhatsApp (Clickable)
                                    if (!user?.phoneNumber.isNullOrEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    user?.phoneNumber?.let { phone ->
                                                        val cleanNumber = phone.replace(Regex("[^0-9]"), "")
                                                        if (cleanNumber.isNotEmpty()) {
                                                            try {
                                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                                    data = Uri.parse("https://wa.me/$cleanNumber")
                                                                    setPackage("com.whatsapp")
                                                                }
                                                                context.startActivity(intent)
                                                            } catch (e: android.content.ActivityNotFoundException) {
                                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                                    data = Uri.parse("https://wa.me/$cleanNumber")
                                                                }
                                                                context.startActivity(intent)
                                                            }
                                                        }
                                                    }
                                                }
                                                .background(Color(0xFF25D366).copy(alpha = 0.15f))
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Phone,
                                                contentDescription = "WhatsApp",
                                                tint = Color(0xFF25D366),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = user?.phoneNumber ?: "",
                                                fontSize = 14.sp,
                                                color = Black,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Products Title
                        if (products.isNotEmpty()) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                Text(
                                    text = "PRODUK DIJUAL",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrayDark,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                )
                            }
                        }

                        // Products
                        if (products.isNotEmpty()) {
                            items(
                                items = products,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = {
                                        product.id.toIntOrNull()?.let { productId ->
                                            if (productId > 0) {
                                                onNavigateToProductDetail(productId)
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            // Empty State
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Belum Ada Produk",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GrayDark
                                    )
                                    Text(
                                        text = "Penjual ini belum mengunggah produk",
                                        fontSize = 14.sp,
                                        color = GrayDark,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Profil tidak ditemukan",
                            color = GrayDark,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onProductClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val imageUrl = product.images?.firstOrNull()?.url ?: product.imageUrl ?: ""
                
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GrayLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada gambar",
                            fontSize = 12.sp,
                            color = GrayDark
                        )
                    }
                }
            }

            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Text(
                    text = product.price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
        }
    }
}
