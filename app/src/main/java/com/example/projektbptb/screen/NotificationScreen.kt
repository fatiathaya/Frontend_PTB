package com.example.projektbptb.screen

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {},
    onNavigateToProductDetail: (Int) -> Unit = {},
    viewModel: NotificationViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as Application
        )
    )
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Load notifications when screen is opened
    LaunchedEffect(Unit) {
        android.util.Log.d("NotificationScreen", "Screen opened, loading notifications...")
        viewModel.loadNotifications()
    }
    
    // Refresh notifications when screen comes into focus
    DisposableEffect(Unit) {
        android.util.Log.d("NotificationScreen", "NotificationScreen composable created")
        onDispose {
            android.util.Log.d("NotificationScreen", "NotificationScreen composable disposed")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(BluePrimary, BlueLight)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                    Text(
                        "Notifikasi",
                                color = Black,
                        fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            if (notifications.isNotEmpty()) {
                                Text(
                                    "${notifications.count { !it.is_read }} baru",
                                    color = GrayDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                    )
                            }
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
                actions = {
                    if (notifications.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.markAllAsRead {
                                    viewModel.loadNotifications()
                                }
                            }
                        ) {
                            Text(
                                "Tandai Semua",
                                color = BluePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            White,
                            GrayLight.copy(alpha = 0.3f)
                        )
                    )
                )
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
                                viewModel.loadNotifications()
                            }
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                notifications.isEmpty() -> {
                    // Empty State - Modern Design
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            BlueLight.copy(alpha = 0.3f),
                                            BlueLight.copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "No Notifications",
                                tint = BluePrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(56.dp)
                        )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Belum Ada Notifikasi",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Notifikasi akan muncul di sini\nketika ada aktivitas baru",
                            fontSize = 14.sp,
                            color = GrayDark,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(
                            items = notifications,
                            key = { it.id }
                        ) { notification ->
                            NotificationItem(
                                notification = notification,
                                onItemClick = {
                                    if (!notification.is_read) {
                                        viewModel.markAsRead(notification.id) {
                                            viewModel.loadNotifications()
                                        }
                                    }
                                    onNavigateToProductDetail(notification.product_id)
                                }
                            )
                            // Divider antar notifikasi (kecuali item terakhir)
                            if (notification != notifications.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = GrayLight,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: com.example.projektbptb.data.network.NotificationResponse,
    onItemClick: () -> Unit
) {
    // Tentukan apakah ini wishlist atau komentar untuk styling berbeda
    val isWishlist = notification.type == "wishlist"
    val isComment = notification.type == "comment" || notification.type == "reply"
    
    // Background berbeda untuk unread
    val backgroundColor = if (notification.is_read) {
        White
    } else {
        when {
            isWishlist -> Color(0xFFFFF5F5) // Light red background untuk wishlist unread
            isComment -> BlueLight.copy(alpha = 0.08f) // Light blue untuk comment unread
            else -> GrayLight.copy(alpha = 0.5f)
        }
    }
    
    // Border accent untuk unread
    val borderColor = if (!notification.is_read) {
        when {
            isWishlist -> Color(0xFFFF5252).copy(alpha = 0.3f)
            isComment -> BluePrimary.copy(alpha = 0.3f)
            else -> BluePrimary.copy(alpha = 0.2f)
        }
    } else {
        Color.Transparent
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(
                onClick = onItemClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .then(
                if (!notification.is_read) {
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    borderColor,
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(0.dp)
                        )
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar/Icon - Gaya Instagram (circular untuk wishlist, rounded untuk comment)
            if (notification.product_image != null) {
                Box(
                    modifier = Modifier
                        .size(if (isWishlist) 56.dp else 48.dp)
                        .clip(
                            if (isWishlist) CircleShape else RoundedCornerShape(12.dp)
                        )
                        .background(White)
                        .then(
                            if (!notification.is_read) {
                                Modifier
                                    .size(if (isWishlist) 60.dp else 52.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = when {
                                                isWishlist -> listOf(
                                                    Color(0xFFFF5252).copy(alpha = 0.2f),
                                                    Color.Transparent
                                                )
                                                isComment -> listOf(
                                                    BluePrimary.copy(alpha = 0.2f),
                                                    Color.Transparent
                                                )
                                                else -> listOf(Color.Transparent)
                                            }
                                        ),
                                        shape = if (isWishlist) CircleShape else RoundedCornerShape(14.dp)
                                    )
                                    .padding(2.dp)
                            } else {
                                Modifier
                            }
                        )
                ) {
                AsyncImage(
                    model = notification.product_image,
                    contentDescription = "Product Image",
                    modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                if (isWishlist) CircleShape else RoundedCornerShape(10.dp)
                            ),
                    contentScale = ContentScale.Crop
                )
                }
            } else {
                // Icon dengan styling berbeda untuk wishlist vs comment
                Box(
                    modifier = Modifier
                        .size(if (isWishlist) 56.dp else 48.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = when {
                                    isWishlist -> listOf(
                                        Color(0xFFFFE5E5),
                                        Color(0xFFFFCCCC)
                                    )
                                    isComment -> listOf(
                                        BlueLight.copy(alpha = 0.5f),
                                        BlueLight.copy(alpha = 0.3f)
                                    )
                                    else -> listOf(
                                        GrayLight,
                                        GrayLight.copy(alpha = 0.7f)
                                    )
                                }
                            ),
                            shape = if (isWishlist) CircleShape else RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isWishlist -> Icons.Default.Favorite
                            isComment -> Icons.Default.Comment
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = when {
                            isWishlist -> Color(0xFFFF5252)
                            isComment -> BluePrimary
                            else -> GrayDark
                        },
                        modifier = Modifier.size(if (isWishlist) 28.dp else 24.dp)
                    )
                }
            }

            // Notification Content - Gaya Instagram
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Message dengan styling berbeda
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Black,
                    maxLines = if (isWishlist) 2 else 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    fontWeight = if (!notification.is_read) FontWeight.Medium else FontWeight.Normal
                )

                // Product name dan timestamp dalam satu baris (gaya Instagram)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notification.product_name != null) {
                    Text(
                            text = notification.product_name,
                            fontSize = 13.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = GrayDark.copy(alpha = 0.5f)
                )
                    }
                    Text(
                        text = formatTimestamp(notification.timestamp ?: notification.created_at),
                        fontSize = 12.sp,
                        color = GrayDark.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Normal
                    )
                }
                }

            // Unread indicator - Gaya Instagram (dot kecil di kanan)
            if (!notification.is_read) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when {
                                isWishlist -> Color(0xFFFF5252)
                                isComment -> BluePrimary
                                else -> BluePrimary
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

fun formatTimestamp(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timestamp)
        date?.let {
            val now = Date()
            val diff = now.time - it.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                seconds < 60 -> "Baru saja"
                minutes < 60 -> "$minutes menit yang lalu"
                hours < 24 -> "$hours jam yang lalu"
                days < 7 -> "$days hari yang lalu"
                else -> {
                    val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    displayFormat.format(it)
                }
            }
        } ?: timestamp
    } catch (e: Exception) {
        timestamp
    }
}
