package com.example.projektbptb.screen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projektbptb.R
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val currentUser by viewModel.user
    var username by remember(currentUser.username) { mutableStateOf(if (currentUser.username == "XXXXXXXXXXX" || currentUser.username.isEmpty()) "M.Abdul" else currentUser.username) }
    var phoneNumber by remember(currentUser.phoneNumber) { mutableStateOf(if (currentUser.phoneNumber.isEmpty()) "0857XXXXXXXXX" else currentUser.phoneNumber) }
    var email by remember(currentUser.email) { mutableStateOf(if (currentUser.email.isEmpty()) "abduldullll@gmail.com" else currentUser.email) }
    var selectedGender by remember(currentUser.gender) { mutableStateOf(if (currentUser.gender.isEmpty()) "Laki-laki" else currentUser.gender) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Profile",
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
                            .background(BlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Profile",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    // Camera Icon Overlay
                    IconButton(
                        onClick = { /* TODO: Handle image picker */ },
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
                    text = "Edit",
                    color = BluePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO: Handle edit */ }
                )
            }

            // Form Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Nama Pengguna
                Column {
                    Text(
                        "Nama Pengguna",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
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
                        onValueChange = { phoneNumber = it },
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
                        onValueChange = { email = it },
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

            // Tombol Simpan
            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = username,
                        username = username,
                        email = email,
                        phoneNumber = phoneNumber,
                        gender = selectedGender
                    )
                    onSaveClick(username, phoneNumber, email, selectedGender)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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

