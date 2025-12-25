package com.example.projektbptb.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projektbptb.R
import com.example.projektbptb.ui.components.AddressInput
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.AddProductViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as android.app.Application
        )
    ),
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    
    var productName by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var price by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val selectedImageFiles = remember { mutableStateListOf<File>() }
    
    var showConditionMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isProductCreated by viewModel.isProductCreated
    
    val conditions = listOf("Baru", "Bekas", "Sangat Baik", "Baik", "Cukup")
    val categories = listOf("Pakaian", "Perabotan", "Elektronik", "Perlengkapan Kuliah", "Sepatu")
    val maxImages = 6
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        val remaining = (maxImages - selectedImageUris.size).coerceAtLeast(0)
        val toAdd = uris.take(remaining)

        toAdd.forEach { uri ->
            // Convert URI to File
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "product_image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            selectedImageUris.add(uri)
            selectedImageFiles.add(file)
        }
    }
    
    // Animation states
    var visible by remember { mutableStateOf(false) }
    
    // Animate screen entrance
    LaunchedEffect(Unit) {
        visible = true
    }
    
    // Pulsing animation for image upload button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (selectedImageUris.isEmpty()) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    // Handle product creation success
    LaunchedEffect(isProductCreated) {
        if (isProductCreated) {
            viewModel.resetState()
            onSubmitSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Produk",
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
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Error message with animation
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    errorMessage?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                            text = error,
                            color = androidx.compose.ui.graphics.Color(0xFFC62828),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Foto Produk
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Foto Produk",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    // Counter badge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BlueLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${selectedImageUris.size}/$maxImages",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BluePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                if (selectedImageUris.isNotEmpty()) {
                    // Grid layout for images (2 columns)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((if (selectedImageUris.size <= 2) 200 else if (selectedImageUris.size <= 4) 400 else 600).dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        itemsIndexed(selectedImageUris) { index, uri ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .height(180.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(uri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Selected Product Image ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Cover badge for first image
                                    if (index == 0) {
                                        Card(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.9f)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Cover",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    // Delete button (X) overlay
                                    Card(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = White),
                                        shape = CircleShape,
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                selectedImageFiles.getOrNull(index)?.delete()
                                                if (index < selectedImageFiles.size) selectedImageFiles.removeAt(index)
                                                selectedImageUris.removeAt(index)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Hapus gambar",
                                                tint = RedPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Empty state
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GrayLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "No Image",
                                tint = GrayDark,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum ada foto",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Maksimal $maxImages foto",
                                fontSize = 12.sp,
                                color = GrayDark
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Animated button
                var buttonScale by remember { mutableStateOf(1f) }
                val buttonAnimatedScale by animateFloatAsState(
                    targetValue = buttonScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "buttonScale"
                )
                
                Button(
                    onClick = {
                        buttonScale = 0.9f
                        imagePickerLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(buttonAnimatedScale)
                ) {
                    Text(
                        if (selectedImageUris.isEmpty()) "Pilih Foto" else "Tambah Foto",
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                LaunchedEffect(buttonScale) {
                    if (buttonScale != 1f) {
                        kotlinx.coroutines.delay(100)
                        buttonScale = 1f
                    }
                }
            }

            // Nama Produk
            Column {
                Text(
                    "Nama Produk",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    placeholder = { Text("Masukkan Nama Produk", color = GrayDark) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = BluePrimary,
                        unfocusedIndicatorColor = BluePrimary,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Kondisi
            Column {
                Text(
                    "Kondisi",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = showConditionMenu,
                    onExpandedChange = { showConditionMenu = !showConditionMenu }
                ) {
                    OutlinedTextField(
                        value = condition,
                        onValueChange = {},
                        placeholder = { Text("Pilih Kondisi", color = GrayDark) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showConditionMenu)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = BluePrimary,
                            unfocusedIndicatorColor = BluePrimary,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = showConditionMenu,
                        onDismissRequest = { showConditionMenu = false }
                    ) {
                        conditions.forEach { cond ->
                            DropdownMenuItem(
                                text = { Text(cond) },
                                onClick = {
                                    condition = cond
                                    showConditionMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Kategori
            Column {
                Text(
                    "Kategori",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = !showCategoryMenu }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        placeholder = { Text("Pilih Kategori", color = GrayDark) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = BluePrimary,
                            unfocusedIndicatorColor = BluePrimary,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Deskripsi
            Column {
                Text(
                    "Deskripsi",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Masukkan Deskripsi", color = GrayDark) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = BluePrimary,
                        unfocusedIndicatorColor = BluePrimary,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Harga
            Column {
                Text(
                    "Harga",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = { Text("Masukkan Harga", color = GrayDark) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = BluePrimary,
                        unfocusedIndicatorColor = BluePrimary,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Alamat
            AddressInput(
                address = address,
                onAddressChange = { addr, lat, lng ->
                    address = addr
                    latitude = lat
                    longitude = lng
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // No Whatsapp
            Column {
                Text(
                    "No Whatsapp",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = whatsappNumber,
                    onValueChange = { whatsappNumber = it },
                    placeholder = { Text("Masukkan No Whatsapp", color = GrayDark) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = BluePrimary,
                        unfocusedIndicatorColor = BluePrimary,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Tambah Produk with animation
            var submitButtonScale by remember { mutableStateOf(1f) }
            val submitButtonAnimatedScale by animateFloatAsState(
                targetValue = submitButtonScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "submitButtonScale"
            )
            
            Button(
                onClick = {
                    submitButtonScale = 0.95f
                    viewModel.createProduct(
                        name = productName,
                        category = category,
                        condition = condition,
                        description = description,
                        address = address,
                        latitude = latitude,
                        longitude = longitude,
                        price = price,
                        whatsappNumber = whatsappNumber,
                        imageFiles = selectedImageFiles.toList()
                    )
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(submitButtonAnimatedScale)
            ) {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "buttonContent"
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White
                        )
                    } else {
                        Text(
                            "Tambah Produk",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            LaunchedEffect(submitButtonScale) {
                if (submitButtonScale != 1f) {
                    kotlinx.coroutines.delay(150)
                    submitButtonScale = 1f
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
