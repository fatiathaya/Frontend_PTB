package com.example.projektbptb.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.delay
import com.example.projektbptb.R
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.ProfileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    val currentUser by viewModel.user
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Clear error message when screen is displayed (to avoid showing product errors)
    LaunchedEffect(Unit) {
        // Only clear if it's a product-related error
        if (errorMessage?.contains("produk", ignoreCase = true) == true ||
            errorMessage?.contains("product", ignoreCase = true) == true) {
            viewModel.errorMessage.value = null
        }
    }
    
    var name by remember(currentUser?.name) { mutableStateOf(currentUser?.name ?: "") }
    var username by remember(currentUser?.username) { mutableStateOf(currentUser?.username ?: "") }
    var phoneNumber by remember(currentUser?.phoneNumber) { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var email by remember(currentUser?.email) { mutableStateOf(currentUser?.email ?: "") }
    var selectedGender by remember(currentUser?.gender) { mutableStateOf(currentUser?.gender ?: "Laki-laki") }
    var updateTriggered by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var croppedImageFile by remember { mutableStateOf<File?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }
    
    // Image cropper launcher - crop to circle
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        try {
            if (result.isSuccessful) {
                result.uriContent?.let { uri ->
                    // Convert URI to File
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val file = File(context.cacheDir, "cropped_profile_${System.currentTimeMillis()}.jpg")
                            inputStream.use { input ->
                                file.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            croppedImageFile = file
                            selectedImageUri = uri
                            android.util.Log.d("AddProfile", "Image cropped successfully: ${file.absolutePath}")
                        } else {
                            errorDialogMessage = "Gagal membuka gambar"
                            showErrorDialog = true
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AddProfile", "Error converting URI to File", e)
                        errorDialogMessage = "Gagal memproses gambar: ${e.message}"
                        showErrorDialog = true
                    }
                }
            } else if (result.error != null) {
                android.util.Log.e("AddProfile", "Crop error: ${result.error}")
                errorDialogMessage = "Gagal memotong gambar: ${result.error?.message}"
                showErrorDialog = true
            }
        } catch (e: Exception) {
            android.util.Log.e("AddProfile", "Crop launcher error", e)
            errorDialogMessage = "Terjadi kesalahan: ${e.message}"
            showErrorDialog = true
        }
    }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        try {
            uri?.let {
                android.util.Log.d("AddProfile", "Image selected: $it")
                // Launch cropper with circle crop shape
                try {
                    val cropOptions = CropImageContractOptions(it, CropImageOptions().apply {
                        cropShape = CropImageView.CropShape.OVAL
                        aspectRatioX = 1
                        aspectRatioY = 1
                        fixAspectRatio = true
                        guidelines = CropImageView.Guidelines.ON
                        outputCompressFormat = android.graphics.Bitmap.CompressFormat.JPEG
                        outputCompressQuality = 80
                    })
                    imageCropLauncher.launch(cropOptions)
                } catch (e: Exception) {
                    android.util.Log.e("AddProfile", "Error launching cropper", e)
                    errorDialogMessage = "Gagal membuka editor gambar: ${e.message}"
                    showErrorDialog = true
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AddProfile", "Image picker error", e)
            errorDialogMessage = "Gagal memilih gambar: ${e.message}"
            showErrorDialog = true
        }
    }
    
    // Error dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorDialogMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Picture Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(BlueLight)
                            .border(3.dp, BluePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            // Show cropped image if available
                            selectedImageUri != null -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(selectedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            // Show existing profile image from server
                            currentUser?.profileImageUrl != null -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(currentUser?.profileImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = R.drawable.logo)
                                )
                            }
                            // Show placeholder
                            else -> {
                                Text(
                                    text = currentUser?.name?.firstOrNull()?.toString() ?: "U",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            }
                        }
                    }
                    // Camera Icon Overlay
                    IconButton(
                        onClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .background(BluePrimary, CircleShape)
                            .border(2.dp, White, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Photo",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pilih Foto",
                    color = BluePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }

            // Form Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Nama
                Column {
                    Text(
                        "Nama",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { newValue -> name = newValue },
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
                
                // Username
                Column {
                    Text(
                        "Username",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = username ?: "",
                        onValueChange = { newValue -> username = newValue },
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

                // No HP
                Column {
                    Text(
                        "No HP",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { newValue -> phoneNumber = newValue },
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

                // Email
                Column {
                    Text(
                        "Email",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { newValue -> email = newValue },
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

                // Jenis Kelamin
                Column {
                    Text(
                        "Jenis Kelamin",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedGender = "Laki-laki" },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedGender == "Laki-laki",
                                onClick = { selectedGender = "Laki-laki" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BluePrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Laki-laki", fontSize = 14.sp)
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedGender = "Perempuan" },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedGender == "Perempuan",
                                onClick = { selectedGender = "Perempuan" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BluePrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Perempuan", fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tombol Simpan
            Button(
                onClick = {
                    if (name.isBlank()) {
                        return@Button
                    }
                    updateTriggered = true
                    viewModel.updateProfile(
                        name = name,
                        username = username ?: "",
                        email = email,
                        phoneNumber = phoneNumber ?: "",
                        gender = selectedGender ?: "Laki-laki",
                        imageFile = croppedImageFile
                    )
                },
                enabled = !isLoading && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White
                    )
                } else {
                    Text(
                        "Simpan",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            // Auto navigate back after successful update
            LaunchedEffect(isLoading, errorMessage, updateTriggered) {
                if (updateTriggered && !isLoading && errorMessage == null) {
                    // Update successful, navigate back
                    kotlinx.coroutines.delay(500)
                    onBackClick()
                    updateTriggered = false
                } else if (updateTriggered && !isLoading && errorMessage != null) {
                    // Update failed, don't navigate
                    updateTriggered = false
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

