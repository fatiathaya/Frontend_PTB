package com.example.projektbptb.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projektbptb.R
import com.example.projektbptb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {}
) {
    var productName by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    
    var showConditionMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    
    val conditions = listOf("Baru", "Bekas", "Sangat Baik", "Baik", "Cukup")
    val categories = listOf("Baju", "Perabotan", "Elektronik", "Kulia", "Sepatu")

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
                        .background(White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedPhoto != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Document",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                selectedPhoto ?: "",
                                fontSize = 12.sp,
                                color = Black
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Document",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { selectedPhoto = "Sepatu Adidas.JPG" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pilih Foto", color = White, fontWeight = FontWeight.SemiBold)
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

            // Lokasi
            Column {
                Text(
                    "Lokasi",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Masukkan Lokasi", color = GrayDark) },
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

            // Tombol Tambah Produk
            Button(
                onClick = onSubmitClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Tambah Produk",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

