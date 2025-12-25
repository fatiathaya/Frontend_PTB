package com.example.projektbptb.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import coil.compose.AsyncImage
import com.example.projektbptb.R
import com.example.projektbptb.data.model.Comment
import com.example.projektbptb.data.model.ProductDetail
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.CommentViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductDetail = ProductDetail(
        name = "Beruang",
        category = "Perabotan",
        condition = "Baru",
        price = "Rp 50.000",
        description = "Beruang dengan desain modern, cocok untuk kamar kos. Kondisi masih baru, belum pernah dipakai.",
        location = "Jl. Sudirman No. 123, Jakarta",
        whatsappNumber = "081234567890",
        imageRes = R.drawable.teddy
    ),
    onBackClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {},
    onSellerClick: (Int) -> Unit = {},
    commentViewModel: CommentViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val productId = product.id.toIntOrNull() ?: 0
    
    // State isFavorite harus ter-sync dengan product.isFavorite
    // Jangan update lokal sebelum API call selesai
    var isFavorite by remember(product.id) { mutableStateOf(product.isFavorite) }
    
    // Update isFavorite ketika product berubah
    LaunchedEffect(product.id, product.isFavorite) {
        isFavorite = product.isFavorite
    }
    
    // Tentukan apakah produk milik user sendiri
    // Backend sudah mengirim isOwnProduct dengan benar
    val isOwnProduct = product.isOwnProduct
    
    // Load comments when product is loaded
    LaunchedEffect(productId) {
        if (productId > 0) {
            commentViewModel.loadComments(productId)
        }
    }
    
    // Auto clear error after 5 seconds
    LaunchedEffect(commentViewModel.errorMessage.value) {
        commentViewModel.errorMessage.value?.let {
            kotlinx.coroutines.delay(5000)
            commentViewModel.errorMessage.value = null
        }
    }
    
    val comments = commentViewModel.comments
    val isLoadingComments = commentViewModel.isLoading.value
    val commentError = commentViewModel.errorMessage.value
    
    // Get current user ID for ownership check
    val authRepository = com.example.projektbptb.data.network.AuthRepository(LocalContext.current)
    val currentUserId = authRepository.getUserId()
    
    var commentText by remember { mutableStateOf("") }
    var showCommentInput by remember { mutableStateOf(false) }
    var commentTextFieldFocusRequester = remember { FocusRequester() }
    var editingCommentId by remember { mutableStateOf<Int?>(null) }
    var editingCommentText by remember { mutableStateOf("") }
    var replyingToCommentId by remember { mutableStateOf<Int?>(null) }
    var replyText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Produk",
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
                actions = {
                    // Icon Notifikasi
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = GrayDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Icon Favorite - hanya tampil jika bukan produk milik user sendiri
                    if (!isOwnProduct) {
                        IconButton(onClick = {
                            // Jangan update state lokal dulu, biarkan API yang menentukan
                            // State akan ter-update setelah API call selesai melalui LaunchedEffect
                            onFavoriteClick()
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) RedPrimary else GrayDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
                .verticalScroll(rememberScrollState())
        ) {
            // Gambar Produk - Tampilkan semua gambar jika ada
            val allImages = if (product.images.isNotEmpty()) {
                product.images
            } else if (!product.imageUrl.isNullOrBlank()) {
                listOf(com.example.projektbptb.data.model.ProductImage(0, product.imageUrl))
            } else {
                emptyList()
            }
            
            if (allImages.isNotEmpty()) {
                // Horizontal scroll untuk multiple images
                if (allImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allImages.forEach { image ->
                            AsyncImage(
                                model = image.url,
                                contentDescription = "${product.name} - Image ${image.id}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(0.dp)),
                                error = painterResource(id = R.drawable.logo),
                                placeholder = painterResource(id = R.drawable.logo)
                            )
                        }
                    }
                } else {
                    // Single image - full width
                    AsyncImage(
                        model = allImages[0].url,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        error = painterResource(id = R.drawable.logo),
                        placeholder = painterResource(id = R.drawable.logo)
                    )
                }
            } else if (product.imageRes != 0) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nama Produk
                Text(
                    text = product.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                // Kategori dan Kondisi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = BlueLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 12.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        color = GrayLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = product.condition,
                            fontSize = 12.sp,
                            color = GrayDark,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Harga
                Text(
                    text = product.price,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )

                Divider(color = GrayLight, thickness = 1.dp)

                // Deskripsi
                Column {
                    Text(
                        text = "Deskripsi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = product.description,
                        fontSize = 14.sp,
                        color = GrayDark,
                        lineHeight = 20.sp
                    )
                }

                Divider(color = GrayLight, thickness = 1.dp)

                // Lokasi
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Lokasi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Detail Lokasi
                    if (product.location.isNotEmpty()) {
                        Text(
                            text = product.location,
                            fontSize = 14.sp,
                            color = GrayDark,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // Map untuk menampilkan lokasi
                    if (product.latitude != null && product.longitude != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            val context = LocalContext.current
                            val lifecycleOwner = LocalLifecycleOwner.current
                            var mapView by remember { mutableStateOf<MapView?>(null) }
                            var currentMarker by remember { mutableStateOf<Marker?>(null) }
                            
                            // Initialize OSMDroid
                            LaunchedEffect(Unit) {
                                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
                                Configuration.getInstance().userAgentValue = context.packageName
                            }
                            
                            AndroidView(
                                factory = { ctx ->
                                    MapView(ctx).apply {
                                        mapView = this@apply
                                        
                                        // Configure OSMDroid
                                        setTileSource(TileSourceFactory.MAPNIK)
                                        setMultiTouchControls(true)
                                        minZoomLevel = 3.0
                                        maxZoomLevel = 20.0
                                        
                                        // Set location from product
                                        val productLocation = GeoPoint(product.latitude!!, product.longitude!!)
                                        controller.setZoom(16.0)
                                        controller.setCenter(productLocation)
                                        
                                        // Add compass overlay
                                        val compassOverlay = CompassOverlay(ctx, InternalCompassOrientationProvider(ctx), this)
                                        compassOverlay.enableCompass()
                                        overlays.add(compassOverlay)
                                        
                                        // Add rotation gesture overlay
                                        val rotationGestureOverlay = RotationGestureOverlay(this)
                                        rotationGestureOverlay.isEnabled = true
                                        overlays.add(rotationGestureOverlay)
                                        
                                        // Add marker for product location
                                        val marker = Marker(this)
                                        marker.position = productLocation
                                        marker.title = product.name
                                        marker.snippet = product.location
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        overlays.add(marker)
                                        currentMarker = marker
                                        
                                        invalidate()
                                    }
                                },
                                modifier = Modifier.fillMaxSize(),
                                update = { view ->
                                    // Update map when coordinates change
                                    if (product.latitude != null && product.longitude != null) {
                                        val location = GeoPoint(product.latitude!!, product.longitude!!)
                                        view.controller.animateTo(location)
                                        view.controller.setZoom(16.0)
                                        
                                        // Update marker
                                        currentMarker?.let { view.overlays.remove(it) }
                                        val marker = Marker(view)
                                        marker.position = location
                                        marker.title = product.name
                                        marker.snippet = product.location
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        view.overlays.add(marker)
                                        currentMarker = marker
                                        view.invalidate()
                                    }
                                }
                            )
                            
                            // Handle lifecycle properly - CRITICAL for MapView to work
                            DisposableEffect(lifecycleOwner) {
                                val observer = LifecycleEventObserver { _, event ->
                                    when (event) {
                                        Lifecycle.Event.ON_RESUME -> {
                                            mapView?.onResume()
                                        }
                                        Lifecycle.Event.ON_PAUSE -> {
                                            mapView?.onPause()
                                        }
                                        else -> {}
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)
                                
                                onDispose {
                                    lifecycleOwner.lifecycle.removeObserver(observer)
                                }
                            }
                        }
                    } else if (product.location.isNotEmpty()) {
                        // Jika tidak ada koordinat, tampilkan pesan
                        Text(
                            text = "Peta tidak tersedia untuk lokasi ini",
                            fontSize = 12.sp,
                            color = GrayDark,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Divider(color = GrayLight, thickness = 1.dp)

                // Seller Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                product.userId?.let { onSellerClick(it) }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!product.sellerProfileImage.isNullOrEmpty()) {
                                AsyncImage(
                                    model = product.sellerProfileImage,
                                    contentDescription = "Seller Profile",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(BlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = product.sellerName.firstOrNull()?.toString() ?: "P",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = product.sellerName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Black
                                )
                                Text(
                                    text = if (!product.sellerUsername.isNullOrEmpty()) "@${product.sellerUsername}" else "Penjual",
                                    fontSize = 12.sp,
                                    color = GrayDark
                                )
                            }
                        }
                    }
                    
                    // Nomor WhatsApp - bisa diklik untuk langsung buka WhatsApp
                    if (product.whatsappNumber.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWhatsAppClick() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF25D366) // WhatsApp green
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF25D366),
                                                Color(0xFF128C7E)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // WhatsApp Icon Circle
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                // Nomor WhatsApp Info
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "Hubungi Penjual",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = product.whatsappNumber,
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                // Arrow Icon
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Open WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = GrayLight, thickness = 1.dp)

                // Komentar Section
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCommentInput = true }
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Komentar (${comments.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        if (!showCommentInput) {
                            Text(
                                text = "Tulis komentar",
                                fontSize = 14.sp,
                                color = BluePrimary
                            )
                        }
                    }

                    // Error message
                    commentError?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = error,
                                    fontSize = 12.sp,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { commentViewModel.errorMessage.value = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Input Komentar (muncul hanya ketika showCommentInput = true)
                    if (showCommentInput) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { 
                                    commentText = it
                                    // Clear error when user starts typing
                                    if (commentError != null) {
                                        commentViewModel.errorMessage.value = null
                                    }
                                },
                                placeholder = { Text("Tulis komentar...", color = GrayDark) },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(commentTextFieldFocusRequester),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = BluePrimary,
                                    unfocusedIndicatorColor = GrayLight,
                                    focusedContainerColor = White,
                                    unfocusedContainerColor = White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                maxLines = 3
                            )
                            IconButton(
                                onClick = {
                                    if (commentText.isNotBlank() && productId > 0) {
                                        // Clear error before creating comment
                                        commentViewModel.errorMessage.value = null
                                        commentViewModel.createComment(productId, commentText.trim()) {
                                            commentText = ""
                                            showCommentInput = false
                                            // Reload comments to ensure sync
                                            commentViewModel.loadComments(productId)
                                        }
                                    }
                                },
                                enabled = !isLoadingComments && commentText.isNotBlank() && productId > 0,
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(BluePrimary, CircleShape)
                            ) {
                                if (isLoadingComments) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = White
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Auto focus ketika input muncul
                        LaunchedEffect(showCommentInput) {
                            if (showCommentInput) {
                                commentTextFieldFocusRequester.requestFocus()
                            }
                        }
                    }

                    // Daftar Komentar
                    if (isLoadingComments && comments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = BluePrimary
                            )
                        }
                    } else if (comments.isEmpty()) {
                        Text(
                            text = "Belum ada komentar. Jadilah yang pertama berkomentar!",
                            fontSize = 14.sp,
                            color = GrayDark,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = if (showCommentInput) 0.dp else 8.dp)
                        ) {
                            comments.forEach { comment ->
                                CommentItem(
                                    comment = comment,
                                    isEditing = editingCommentId == comment.id.toIntOrNull(),
                                    editingText = if (editingCommentId == comment.id.toIntOrNull()) editingCommentText else "",
                                    onEditTextChange = { editingCommentText = it },
                                    onEditClick = {
                                        editingCommentId = comment.id.toIntOrNull()
                                        editingCommentText = comment.commentText
                                    },
                                    onSaveEdit = {
                                        comment.id.toIntOrNull()?.let { commentId ->
                                            commentViewModel.updateComment(commentId, editingCommentText.trim()) {
                                                editingCommentId = null
                                                editingCommentText = ""
                                            }
                                        }
                                    },
                                    onCancelEdit = {
                                        editingCommentId = null
                                        editingCommentText = ""
                                    },
                                    onDeleteClick = {
                                        comment.id.toIntOrNull()?.let { commentId ->
                                            commentViewModel.deleteComment(commentId) {}
                                        }
                                    },
                                    onReplyClick = {
                                        replyingToCommentId = comment.id.toIntOrNull()
                                        replyText = ""
                                    },
                                    isReplying = replyingToCommentId == comment.id.toIntOrNull(),
                                    replyText = if (replyingToCommentId == comment.id.toIntOrNull()) replyText else "",
                                    onReplyTextChange = { replyText = it },
                                    onSendReply = {
                                        comment.id.toIntOrNull()?.let { commentId ->
                                            if (replyText.isNotBlank()) {
                                                commentViewModel.createComment(productId, replyText.trim(), parentCommentId = commentId) {
                                                    replyText = ""
                                                    replyingToCommentId = null
                                                    commentViewModel.loadComments(productId)
                                                }
                                            }
                                        }
                                    },
                                    onCancelReply = {
                                        replyingToCommentId = null
                                        replyText = ""
                                    },
                                    currentUserId = currentUserId,
                                    productId = productId,
                                    commentViewModel = commentViewModel
                                )
                                
                                // Tampilkan Replies (nested comments) - di ProductDetailScreen
                                if (comment.replies.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 48.dp), // Indentasi untuk replies
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        comment.replies.forEach { reply ->
                                            CommentItem(
                                                comment = reply,
                                                isEditing = editingCommentId == reply.id.toIntOrNull(),
                                                editingText = if (editingCommentId == reply.id.toIntOrNull()) editingCommentText else "",
                                                onEditTextChange = { editingCommentText = it },
                                                onEditClick = {
                                                    editingCommentId = reply.id.toIntOrNull()
                                                    editingCommentText = reply.commentText
                                                },
                                                onSaveEdit = {
                                                    reply.id.toIntOrNull()?.let { replyId ->
                                                        commentViewModel.updateComment(replyId, editingCommentText.trim()) {
                                                            editingCommentId = null
                                                            editingCommentText = ""
                                                            // Reload comments untuk sync
                                                            commentViewModel.loadComments(productId)
                                                        }
                                                    }
                                                },
                                                onCancelEdit = {
                                                    editingCommentId = null
                                                    editingCommentText = ""
                                                },
                                                onDeleteClick = {
                                                    reply.id.toIntOrNull()?.let { replyId ->
                                                        commentViewModel.deleteComment(replyId) {
                                                            // Reload comments untuk sync
                                                            commentViewModel.loadComments(productId)
                                                        }
                                                    }
                                                },
                                                onReplyClick = {
                                                    replyingToCommentId = reply.id.toIntOrNull()
                                                    replyText = ""
                                                },
                                                isReplying = replyingToCommentId == reply.id.toIntOrNull(),
                                                replyText = if (replyingToCommentId == reply.id.toIntOrNull()) replyText else "",
                                                onReplyTextChange = { replyText = it },
                                                onSendReply = {
                                                    reply.id.toIntOrNull()?.let { replyId ->
                                                        if (replyText.isNotBlank()) {
                                                            commentViewModel.createComment(productId, replyText.trim(), parentCommentId = replyId) {
                                                                replyText = ""
                                                                replyingToCommentId = null
                                                                commentViewModel.loadComments(productId)
                                                            }
                                                        }
                                                    }
                                                },
                                                onCancelReply = {
                                                    replyingToCommentId = null
                                                    replyText = ""
                                                },
                                                currentUserId = currentUserId,
                                                productId = productId,
                                                commentViewModel = commentViewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isEditing: Boolean = false,
    editingText: String = "",
    onEditTextChange: (String) -> Unit = {},
    onEditClick: () -> Unit = {},
    onSaveEdit: () -> Unit = {},
    onCancelEdit: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onReplyClick: () -> Unit = {},
    isReplying: Boolean = false,
    replyText: String = "",
    onReplyTextChange: (String) -> Unit = {},
    onSendReply: () -> Unit = {},
    onCancelReply: () -> Unit = {},
    currentUserId: Int? = null,
    productId: Int = 0,
    commentViewModel: CommentViewModel? = null
) {
    val replyTextFieldFocusRequester = remember { FocusRequester() }
    // Check if current user owns this comment
    // For now, we'll check by userId (will be improved with actual auth)
    val isOwnComment = currentUserId != null && comment.userId == currentUserId
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.userName.first().toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.timestamp,
                            fontSize = 12.sp,
                            color = GrayDark
                        )
                        // Tombol Edit dan Delete hanya untuk komentar sendiri
                        if (isOwnComment && !isEditing) {
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                if (isEditing) {
                    // Mode Edit: Tampilkan TextField
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editingText,
                            onValueChange = onEditTextChange,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = BluePrimary,
                                unfocusedIndicatorColor = GrayLight,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 3
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onCancelEdit) {
                                Text("Batal", color = GrayDark)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onSaveEdit,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Simpan", color = White)
                            }
                        }
                    }
                } else {
                    // Mode Normal: Tampilkan teks komentar
                    Text(
                        text = comment.commentText,
                        fontSize = 14.sp,
                        color = Black,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tombol Balas (selalu muncul, tidak peduli own comment atau tidak)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onReplyClick,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text(
                            text = "Balas",
                            fontSize = 12.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Input Reply (muncul saat isReplying = true)
                if (isReplying) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = onReplyTextChange,
                            placeholder = { Text("Balas ${comment.userName}...", color = GrayDark, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(replyTextFieldFocusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = BluePrimary,
                                unfocusedIndicatorColor = GrayLight,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 2,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                        IconButton(
                            onClick = onSendReply,
                            enabled = replyText.isNotBlank(),
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (replyText.isNotBlank()) BluePrimary else GrayLight,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send Reply",
                                tint = White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onCancelReply,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = GrayDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    // Auto focus ketika reply input muncul
                    LaunchedEffect(isReplying) {
                        if (isReplying) {
                            replyTextFieldFocusRequester.requestFocus()
                        }
                    }
                }
                
                // Tampilkan Replies (nested comments) - dipindahkan ke ProductDetailScreen
                // Replies akan ditampilkan di ProductDetailScreen, bukan di sini
            }
        }
    }
}

