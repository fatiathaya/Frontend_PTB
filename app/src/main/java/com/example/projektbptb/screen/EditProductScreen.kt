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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var category by remember { mutableStateOf(product.category) }
    var description by remember { mutableStateOf(product.description ?: "") }
    var price by remember { mutableStateOf(product.price.replace("Rp ", "").replace(".", "").trim()) }
    var whatsappNumber by remember { mutableStateOf(product.whatsappNumber ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    
    // Clear any previous success/error messages when screen opens
    LaunchedEffect(Unit) {
        viewModel.clearMessages()
    }
    
    // Debug log
    LaunchedEffect(product) {
        android.util.Log.d("EditProductScreen", "Initializing with product: " +
                "name='$productName', category='$category', condition='$condition', " +
                "description='$description', price='$price', whatsapp='$whatsappNumber'")
    }
    
    // Show success message and navigate back
    // Both update and delete will auto-navigate back to Profile
    LaunchedEffect(viewModel.successMessage.value) {
        val message = viewModel.successMessage.value
        if (!message.isNullOrEmpty()) {
            // Show snackbar first
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.successMessage.value = null
            
            // Navigate back after showing message (for both update and delete)
            kotlinx.coroutines.delay(500) // Give time to see the message
            onBackClick()
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
    val categories = listOf("Baju", "Perabotan", "Elektronik", "Kulia", "Sepatu")
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Convert URI to File
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "product_image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            selectedImageFile = file
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
                    Text(
                        "Foto Produk",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(1.dp, Black, RoundedCornerShape(8.dp))
                            .background(White, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = selectedImageUri ?: product.imageUrl,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                        fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                            },
                            label = "imageTransition"
                        ) { imageSource ->
                            when {
                                selectedImageUri != null -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(selectedImageUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Selected Product Image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                product.imageUrl != null -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(product.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Current Product Image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                product.imageRes != 0 -> {
                                    Image(
                                        painter = painterResource(id = product.imageRes),
                                        contentDescription = "Current Product Image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
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
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
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
                        Text("Ubah Foto", color = White, fontWeight = FontWeight.SemiBold)
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
                                    whatsappNumber = whatsappNumber.ifBlank { null },
                                    imageUrl = product.imageUrl // Keep existing image URL if no new image
                                )
                                // Pass both updated product and image file (if any)
                                onEditClick(updatedProduct, selectedImageFile)
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
