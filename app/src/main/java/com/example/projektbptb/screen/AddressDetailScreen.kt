package com.example.projektbptb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projektbptb.data.model.Address
import com.example.projektbptb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDetailScreen(
    address: Address? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (Address) -> Unit = {}
) {
    var locationName by remember { mutableStateOf(address?.locationName ?: "Kost Bang Am") }
    var fullAddress by remember { mutableStateOf(address?.fullAddress ?: "MAC 2024, Kec. Pauh, Kota Padang, Sumatera Barat, Indonesia") }
    var detailLocation by remember { mutableStateOf(address?.detailLocation ?: "") }
    var landmark by remember { mutableStateOf(address?.landmark ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (address != null) "Edit Alamat" else "Tambah Alamat",
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
        ) {
            // Map Section (Placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFFF5F5F5))
            ) {
                // Placeholder for map - in real app, this would be Google Maps
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Map View",
                            fontSize = 14.sp,
                            color = GrayDark
                        )
                    }
                }
            }

            // Address Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Detail Alamat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                locationName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Black
                            )
                            Text(
                                fullAddress,
                                fontSize = 12.sp,
                                color = GrayDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Detail Lokasi (optional)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Detail lokasi (optional)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = detailLocation,
                            onValueChange = { detailLocation = it },
                            placeholder = { Text("Nama bangunan/no.unit/lantai", color = GrayDark) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = BluePrimary,
                                unfocusedIndicatorColor = GrayLight,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Patokan (optional)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Patokan (optional)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            placeholder = { Text("Sebelah toko grosir", color = GrayDark) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = BluePrimary,
                                unfocusedIndicatorColor = GrayLight,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val newAddress = Address(
                        id = address?.id ?: "",
                        label = address?.label ?: "Rumah",
                        fullAddress = fullAddress,
                        locationName = locationName,
                        detailLocation = detailLocation,
                        landmark = landmark
                    )
                    onSaveClick(newAddress)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Simpan",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

