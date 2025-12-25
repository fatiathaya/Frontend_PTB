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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projektbptb.R
import com.example.projektbptb.data.model.Product
import com.example.projektbptb.ui.components.AddressInput
import com.example.projektbptb.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    product: Product,
    viewModel: com.example.projektbptb.viewmodel.ProfileViewModel,
    onBackClick: () -> Unit = {},
    onEditClick: (Product, File?) -> Unit = { _, _ -> },
    onDeleteClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Pre-fill with existing product data
    var productName by remember { mutableStateOf(product.name) }
    var condition by remember { mutableStateOf(product.condition ?: "Bekas") }
    var category by remember {
        mutableStateOf(
            when (product.category) {
                "Baju" -> "Pakaian"
                "Kulia" -> "Perlengkapan Kuliah"
                else -> product.category
            }
        )
    }
    var description by remember { mutableStateOf(product.description ?: "") }
    var address by remember { mutableStateOf(product.address ?: "") }
    var latitude by remember { mutableStateOf(product.latitude) }
    var longitude by remember { mutableStateOf(product.longitude) }
    var price by remember { mutableStateOf(product.price.replace("Rp ", "").replace(".", "").trim()) }
    var whatsappNumber by remember { mutableStateOf(product.whatsappNumber ?: "") }
    // Multi-image support
    data class ImageItem(
        val id: String, // "existing_$id" or "new_$timestamp"
        val uri: Uri? = null,
        val url: String? = null,
        val file: File? = null,
        val isExisting: Boolean = false
    )
    
    val selectedImages = remember { mutableStateListOf<ImageItem>() }
    
    // CRITICAL: Store original product images to track deletions
    // This is used to determine which images were deleted when user clicks Save
    val originalProductImages = remember(product.id) {
        val images = product.images.toList()
        android.util.Log.d("EditProductScreen", "Storing original images: ${images.size} images with IDs: ${images.map { it.id }}")
        images
    }
    
    // CRITICAL: Initialize selectedImages when screen first opens
    // This runs when product.id changes (when navigating to this screen)
    LaunchedEffect(product.id) {
        if (product.id.isNotEmpty()) {
            android.util.Log.d("EditProductScreen", "=== INITIALIZING SCREEN ===")
            android.util.Log.d("EditProductScreen", "Product loaded - id=${product.id}, images=${product.images.size}, image IDs=${product.images.map { it.id }}")
            
            // Clear all images first
            selectedImages.clear()
            
            // Load all existing images from product.images (multi-image support)
            if (product.images.isNotEmpty()) {
                product.images.forEach { img ->
                    selectedImages.add(ImageItem("existing_${img.id}", url = img.url, isExisting = true))
                }
            } else {
                // Fallback to single imageUrl for backward compatibility
                product.imageUrl?.let { url ->
                    selectedImages.add(ImageItem("existing_0", url = url, isExisting = true))
                }
            }
            
            android.util.Log.d("EditProductScreen", "Initialized selectedImages: ${selectedImages.size} images")
            android.util.Log.d("EditProductScreen", "Original images stored: ${originalProductImages.size} images with IDs: ${originalProductImages.map { it.id }}")
        }
    }
    
    
    // ========================================
    // LaunchedEffect(productImagesKey) DIHAPUS
    // Ini menyebabkan INFINITE LOOP!
    // JANGAN DITAMBAHKAN KEMBALI!
    // ========================================
    
    // Clear any previous success/error messages when screen opens
    LaunchedEffect(Unit) {
        viewModel.clearMessages()
    }
    
    // Debug log
    LaunchedEffect(product) {
        android.util.Log.d("EditProductScreen", "Initializing with product: " +
                "id=${product.id}, name='$productName', category='$category', condition='$condition', " +
                "description='$description', price='$price', whatsapp='$whatsappNumber', " +
                "images=${product.images.size}")
    }
    
    // Show success message - STAY on edit screen (no navigation)
    LaunchedEffect(viewModel.successMessage.value) {
        val message = viewModel.successMessage.value
        if (!message.isNullOrEmpty()) {
            // Show snackbar only
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.successMessage.value = null
            // NO onBackClick() - stay on edit screen
        }
    }
    
    // Show error message
    LaunchedEffect(viewModel.errorMessage.value) {
        val message = viewModel.errorMessage.value
        if (!message.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.errorMessage.value = null
        }
    }
    
    var showConditionMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val conditions = listOf("Baru", "Bekas", "Sangat Baik", "Baik", "Cukup")
    val categories = listOf("Pakaian", "Perabotan", "Elektronik", "Perlengkapan Kuliah", "Sepatu")
    
    // Multi-image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val timestamp = System.currentTimeMillis()
            val file = File(context.cacheDir, "product_image_$timestamp.jpg")
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                selectedImages.add(ImageItem("new_$timestamp", uri = uri, file = file, isExisting = false))
            } catch (e: Exception) {
                android.util.Log.e("EditProductScreen", "Error saving image: ${e.message}")
            }
        }
    }
    
    // Animation states
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Produk") },
            text = { Text("Apakah Anda yakin ingin menghapus produk \"${product.name}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    enabled = !viewModel.isUpdating.value
                ) {
                    if (viewModel.isUpdating.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Hapus")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !viewModel.isUpdating.value
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (data.visuals.message.contains("berhasil", ignoreCase = true) || 
                                        data.visuals.message.contains("dihapus", ignoreCase = true) ||
                                        data.visuals.message.contains("diperbarui", ignoreCase = true)) 
                                     GreenPrimary else RedPrimary,
                    contentColor = White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Produk",
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
                                text = "${selectedImages.size}/6",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BluePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    if (selectedImages.isNotEmpty()) {
                        // Grid layout for images (2 columns)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((if (selectedImages.size <= 2) 200 else if (selectedImages.size <= 4) 400 else 600).dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            itemsIndexed(selectedImages) { index, imageItem ->
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
                                        when {
                                            imageItem.uri != null -> {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(imageItem.uri)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Selected Product Image ${index + 1}",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            imageItem.url != null -> {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(imageItem.url)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Existing Product Image ${index + 1}",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            else -> {
                                                Image(
                                                    painter = painterResource(id = R.drawable.logo),
                                                    contentDescription = "No Image",
                                                    modifier = Modifier.size(48.dp)
                                                )
                                            }
                                        }

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
                                                    try {
                                                        android.util.Log.d("EditProductScreen", "Deleting image: id=${imageItem.id}, isExisting=${imageItem.isExisting}, index=$index")
                                                        
                                                        if (!imageItem.isExisting) {
                                                            // New image (not yet saved) - just remove from list and delete file
                                                            imageItem.file?.delete()
                                                            if (index >= 0 && index < selectedImages.size) {
                                                                selectedImages.removeAt(index)
                                                            }
                                                            android.util.Log.d("EditProductScreen", "Deleted new image file: ${imageItem.file?.name}")
                                                        } else {
                                                            // Existing image - delete from database immediately
                                                            val imageId = imageItem.id.removePrefix("existing_").toIntOrNull()
                                                            if (imageId != null && product.id.isNotEmpty()) {
                                                                android.util.Log.d("EditProductScreen", "Deleting existing image from database: imageId=$imageId, productId=${product.id}")
                                                                viewModel.deleteProductImage(product.id, imageId)
                                                                
                                                                // Remove from UI immediately (will be updated when response comes)
                                                                if (index >= 0 && index < selectedImages.size) {
                                                                    selectedImages.removeAt(index)
                                                                }
                                                            } else {
                                                                android.util.Log.e("EditProductScreen", "Cannot delete: invalid imageId=$imageId or productId=${product.id}")
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        android.util.Log.e("EditProductScreen", "Error deleting image: ${e.message}", e)
                                                    }
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
                                    text = "Maksimal 6 foto",
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
                            if (selectedImages.size < 6) {
                                imagePickerLauncher.launch("image/*")
                            }
                        },
                        enabled = selectedImages.size < 6,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            disabledContainerColor = GrayDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(buttonAnimatedScale)
                    ) {
                        Text(
                            if (selectedImages.isEmpty()) "Pilih Foto" else "Tambah Foto",
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

                // Tombol Edit dan Hapus with animation
                var editButtonScale by remember { mutableStateOf(1f) }
                val editButtonAnimatedScale by animateFloatAsState(
                    targetValue = editButtonScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "editButtonScale"
                )
                
                var deleteButtonScale by remember { mutableStateOf(1f) }
                val deleteButtonAnimatedScale by animateFloatAsState(
                    targetValue = deleteButtonScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "deleteButtonScale"
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (!viewModel.isUpdating.value) {
                                editButtonScale = 0.95f
                                val updatedProduct = product.copy(
                                    name = productName,
                                    category = category,
                                    price = "Rp $price",
                                    description = description.ifBlank { null },
                                    condition = condition,
                                    address = address.ifBlank { null },
                                    latitude = latitude,
                                    longitude = longitude,
                                    whatsappNumber = whatsappNumber.ifBlank { null },
                                    imageUrl = product.imageUrl // Keep existing image URL if no new image
                                )
                                
                                // Separate new images and existing images to delete
                                val newImageFiles = selectedImages.filter { !it.isExisting && it.file != null }.mapNotNull { it.file }
                                
                                // CRITICAL: Track which existing images were deleted
                                // Use originalProductImages (stored when screen opened) to track deletions
                                // This ensures we track deletions correctly even if product.images changes
                                val originalImageIds = originalProductImages.map { it.id }.toSet()
                                
                                // currentExistingIds = image IDs that are still in selectedImages (not deleted by user)
                                val currentExistingIds = selectedImages
                                    .filter { it.isExisting }
                                    .mapNotNull { it.id.removePrefix("existing_").toIntOrNull() }
                                    .toSet()
                                
                                // deleteImageIds = images that were in original but are NOT in current (deleted by user)
                                val deleteImageIds = originalImageIds.filter { it !in currentExistingIds }
                                
                                android.util.Log.d("EditProductScreen", "=== SUBMITTING UPDATE ===")
                                android.util.Log.d("EditProductScreen", "Original image IDs (from product): $originalImageIds")
                                android.util.Log.d("EditProductScreen", "Current existing IDs (still in UI): $currentExistingIds")
                                android.util.Log.d("EditProductScreen", "Images to DELETE from database: $deleteImageIds")
                                android.util.Log.d("EditProductScreen", "New images to UPLOAD: ${newImageFiles.size}")
                                
                                // CRITICAL: Ensure deleteImageIds is sent even if empty list
                                // Backend expects null or array, so we send null if empty, or list if not empty
                                val deleteIdsToSend = if (deleteImageIds.isNotEmpty()) {
                                    deleteImageIds.toList()
                                } else {
                                    null
                                }
                                
                                // Pass updated product, new image files, and delete IDs
                                viewModel.updateProduct(
                                    oldProduct = product,
                                    newProduct = updatedProduct,
                                    imageFiles = newImageFiles.ifEmpty { null },
                                    deleteImageIds = deleteIdsToSend
                                )
                            }
                        },
                        enabled = !viewModel.isUpdating.value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            disabledContainerColor = BluePrimary.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .scale(editButtonAnimatedScale)
                    ) {
                        AnimatedContent(
                            targetState = viewModel.isUpdating.value,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                            },
                            label = "SaveButtonContent"
                        ) { isUpdating ->
                            if (isUpdating) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Menyimpan...",
                                        color = White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            } else {
                                Text(
                                    "Simpan",
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = {
                            deleteButtonScale = 0.95f
                            showDeleteDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .scale(deleteButtonAnimatedScale)
                    ) {
                        Text(
                            "Hapus",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                LaunchedEffect(editButtonScale) {
                    if (editButtonScale != 1f) {
                        kotlinx.coroutines.delay(150)
                        editButtonScale = 1f
                    }
                }
                
                LaunchedEffect(deleteButtonScale) {
                    if (deleteButtonScale != 1f) {
                        kotlinx.coroutines.delay(150)
                        deleteButtonScale = 1f
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
