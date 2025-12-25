package com.example.projektbptb.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.projektbptb.data.model.Product
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projektbptb.R
import com.example.projektbptb.ui.component.BottomNavigationBar
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToSell: () -> Unit = {},
    onNavigateToAddProfile: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToEditProduct: (com.example.projektbptb.data.model.Product) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val user by viewModel.user
    val notifEnabled by viewModel.isNotificationEnabled
    
    // Refresh user data and products when screen is displayed
    DisposableEffect(Unit) {
        // Always clear messages when returning to ProfileScreen
        viewModel.clearMessages()
        
        viewModel.loadUser()
        viewModel.loadMyProducts() // Reload products to show latest changes
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "User",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BluePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
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
            val isProfileComplete = user?.let { 
                !it.email.isNullOrBlank() && !it.phoneNumber.isNullOrBlank()
            } ?: false
            BottomNavigationBar(
                currentRoute = "settings",
                isProfileComplete = isProfileComplete,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSell = onNavigateToSell,
                onNavigateToSettings = {},
                onNavigateToCompleteProfile = onNavigateToAddProfile
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
            Spacer(modifier = Modifier.height(20.dp))

            // Warning Card for Incomplete Profile
            val isProfileComplete = user?.let { 
                !it.email.isNullOrBlank() && !it.phoneNumber.isNullOrBlank()
            } ?: false
            
            if (!isProfileComplete && user != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OrangePrimary.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Profil Belum Lengkap!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                        }
                        
                        Text(
                            "Lengkapi profil Anda untuk bisa menjual produk:",
                            fontSize = 14.sp,
                            color = GrayDark
                        )
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (user?.email.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        tint = RedPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Email belum diisi",
                                        fontSize = 13.sp,
                                        color = GrayDark
                                    )
                                }
                            }
                            if (user?.phoneNumber.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        tint = RedPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Nomor telepon belum diisi",
                                        fontSize = 13.sp,
                                        color = GrayDark
                                    )
                                }
                            }
                        }
                        
                        Button(
                            onClick = onNavigateToAddProfile,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Lengkapi Sekarang",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Avatar dengan Edit Icon
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                user?.let { currentUser ->
                    Box {
                        // Display profile image - prioritize server URL over local resource
                        when {
                            currentUser.profileImageUrl != null -> {
                                AsyncImage(
                                    model = currentUser.profileImageUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, GreenPrimary, CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    error = painterResource(id = R.drawable.logo)
                                )
                            }
                            currentUser.profileImageRes != 0 -> {
                                Image(
                                    painter = painterResource(id = currentUser.profileImageRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, GreenPrimary, CircleShape)
                                )
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(BlueLight)
                                        .border(2.dp, GreenPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.name.firstOrNull()?.toString() ?: "U",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                            }
                        }
                        // Edit Icon di pojok kanan bawah
                        IconButton(
                            onClick = { onNavigateToAddProfile() },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .background(BluePrimary, CircleShape)
                                .border(2.dp, White, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Photo",
                                tint = White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentUser.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    Text(
                        text = currentUser.gender ?: "",
                        fontSize = 14.sp,
                        color = GrayDark
                    )
                } ?: run {
                    // Show loading or placeholder when user is null
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Edit Profile dan Hapus Foto Profile - hanya tampil jika profil lengkap
            if (isProfileComplete) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onNavigateToAddProfile() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "Edit Profile",
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.deleteProfileImage() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        enabled = user?.profileImageUrl != null
                    ) {
                        Text(
                            "Hapus Foto Profile",
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Details dengan Icon
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Divider(color = GrayLight, thickness = 1.dp)
                
                // Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = GrayDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        user?.email ?: "",
                        fontSize = 14.sp,
                        color = Black
                    )
                }
                
                Divider(color = GrayLight, thickness = 1.dp)
                
                // Phone Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = GrayDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        user?.phoneNumber ?: "",
                        fontSize = 14.sp,
                        color = Black
                    )
                }
                
                Divider(color = GrayLight, thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Produk Saya
            Text(
                "Produk Saya",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val myProducts = viewModel.myProducts
            val deletingProductId by viewModel.deletingProductId
            
            if (myProducts.isEmpty()) {
                Text(
                    "Belum ada produk",
                    fontSize = 14.sp,
                    color = GrayDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            } else {
                myProducts.forEach { product ->
                    // Animated product item with slide-out and fade-out animation
                    AnimatedVisibility(
                        visible = deletingProductId != product.id,
                        exit = slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        ) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        )
                    ) {
                        ProductItem(
                            product = product,
                            onEditClick = { onNavigateToEditProduct(product) },
                            onDeleteClick = { viewModel.deleteProduct(product) }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = GrayDark
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Black
        )
    }
}

@Composable
fun ProductItem(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Delete confirmation dialog with animation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
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
                    "Hapus Produk?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus \"${product.name}\"? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = GrayLight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use AsyncImage for URL images, fallback to resource
            if (!product.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    error = painterResource(id = com.example.projektbptb.R.drawable.logo),
                    placeholder = painterResource(id = com.example.projektbptb.R.drawable.logo)
                )
            } else if (product.imageRes != 0) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image(
                    painter = painterResource(id = com.example.projektbptb.R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
                Text(
                    product.category,
                    fontSize = 12.sp,
                    color = GrayDark
                )
                Text(
                    product.price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Text(
                        "⋮",
                        fontSize = 24.sp,
                        color = GrayDark
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus", color = RedPrimary) },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = RedPrimary
                            )
                        }
                    )
                }
            }
        }
    }
}

