package com.example.projektbptb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projektbptb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ganti Password",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Password Lama
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Password Lama", color = GrayDark) },
                placeholder = { Text("Masukkan password lama", color = GrayDark) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = if (showCurrentPassword) "Hide password" else "Show password",
                            tint = GrayDark
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = BluePrimary,
                    unfocusedIndicatorColor = BluePrimary,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Password Baru
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Password Baru", color = GrayDark) },
                placeholder = { Text("Masukkan password baru", color = GrayDark) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNewPassword = !showNewPassword }) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = if (showNewPassword) "Hide password" else "Show password",
                            tint = GrayDark
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = BluePrimary,
                    unfocusedIndicatorColor = BluePrimary,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Konfirmasi Password Baru
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Password Baru", color = GrayDark) },
                placeholder = { Text("Masukkan ulang password baru", color = GrayDark) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                            tint = GrayDark
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = BluePrimary,
                    unfocusedIndicatorColor = BluePrimary,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    // TODO: Handle change password
                    onBackClick()
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
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

