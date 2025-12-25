package com.example.projektbptb.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projektbptb.R
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: HomeViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onViewProductClick: (Product) -> Unit = {}
) {
    val favoriteProducts = viewModel.favoriteProducts
    val isLoadingFavorites = viewModel.isLoadingFavorites
    
    // Always reload favorites when this screen is displayed
    // Use a key that changes to force reload when navigating to this screen
    var reloadTrigger by remember { mutableStateOf(0) }
    
    LaunchedEffect(reloadTrigger) {
        viewModel.loadFavorites()
    }
    
    // Reload when screen becomes visible (when navigating to this screen)
    DisposableEffect(Unit) {
        reloadTrigger++
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Produk Disukai",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
        when {
            isLoadingFavorites.value -> {
                LoadingAnimation(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            favoriteProducts.isEmpty() -> {
                EmptyWishlistState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            else -> {
                val listState = rememberLazyListState()
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrayLight)
                        .padding(innerPadding)
                ) {
                    // Header dengan jumlah produk dan animasi
                    AnimatedContent(
                        targetState = favoriteProducts.size,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                        },
                        label = "count_animation"
                    ) { count ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$count Produk Disukai",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = GrayDark
                            )
                        }
                    }
                    
                    // List produk dengan animasi
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = favoriteProducts,
                            key = { it.id }
                        ) { product ->
                            AnimatedWishlistItem(
                                product = product,
                                onViewClick = { onViewProductClick(product) },
                                onDeleteClick = { 
                                    viewModel.removeFavorite(product)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedWishlistItem(
    product: Product,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    
    // Animasi fade in saat item muncul
    LaunchedEffect(Unit) {
        delay(50) // Stagger animation
        isVisible = true
    }
    
    // Animasi scale untuk delete button
    val deleteButtonScale by animateFloatAsState(
        targetValue = if (isDeleting) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "delete_scale"
    )
    
    // Animasi untuk card
    val cardAlpha by animateFloatAsState(
        targetValue = if (isVisible && !isDeleting) 1f else 0f,
        animationSpec = tween(300),
        label = "card_alpha"
    )
    
    val cardScale by animateFloatAsState(
        targetValue = if (isVisible && !isDeleting) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    AnimatedVisibility(
        visible = !isDeleting,
        exit = slideOutHorizontally(
            animationSpec = tween(400),
            targetOffsetX = { -it }
        ) + fadeOut(animationSpec = tween(400)),
        label = "item_visibility"
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(cardAlpha)
                .scale(cardScale)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(16.dp))
                .clickable { onViewClick() }, // Card dapat diklik untuk masuk ke detail
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Product Image dengan animasi - ukuran lebih kecil dan rapi
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GrayLight)
                ) {
                    if (!product.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.logo),
                            placeholder = painterResource(id = R.drawable.logo)
                        )
                    } else if (product.imageRes != 0) {
                        Image(
                            painter = painterResource(id = product.imageRes),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Product Info - layout rapi dengan delete button di tengah vertikal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight() // Mengisi tinggi yang sama dengan gambar
                ) {
                    // Konten utama: Nama, Category, Price
                Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        // Bagian atas: Nama produk
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    
                        // Bagian tengah: Category dan Price
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                    // Category Badge
                    Surface(
                        color = BlueLight,
                                shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BluePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                            // Price - dengan lineHeight untuk mencegah terpotong
                    Text(
                        text = if (product.price.startsWith("Rp")) product.price else "Rp ${product.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                lineHeight = 22.sp, // Mencegah teks terpotong
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                    // Delete Icon dengan animasi - di kanan tengah (vertically centered)
                    Box(
                    modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                        .scale(deleteButtonScale)
                        .background(
                            color = RedPrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                        )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                isDeleting = true
                                onDeleteClick()
                            },
                        contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus dari wishlist",
                        tint = RedPrimary,
                            modifier = Modifier.size(18.dp)
                    )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = BluePrimary,
                modifier = Modifier
                    .size(64.dp)
                    .scale(scale)
                    .alpha(alpha)
            )
            Text(
                text = "Memuat produk disukai...",
                fontSize = 14.sp,
                color = GrayDark,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptyWishlistState(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty")
    
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Favorite besar dengan animasi
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(iconScale)
                .background(
                    color = BlueLight.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(60.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = BluePrimary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Belum Ada Produk Disukai",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Mulai tambahkan produk favorit Anda\ndengan menekan ikon hati pada produk",
            fontSize = 14.sp,
            color = GrayDark,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Decorative elements
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = BlueLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = "Tekan hati untuk menambah",
                fontSize = 12.sp,
                color = GrayDark,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

