package com.example.projektbptb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.clickable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import com.example.projektbptb.ui.theme.BlueLight
import com.example.projektbptb.ui.theme.BluePrimary
import androidx.compose.ui.platform.LocalContext

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: com.example.projektbptb.viewmodel.LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isRegisterSuccess by viewModel.isRegisterSuccess
    val context = LocalContext.current
    
    LaunchedEffect(isRegisterSuccess) {
        if (isRegisterSuccess) {
            android.widget.Toast.makeText(context, "Registrasi berhasil, silakan login", android.widget.Toast.LENGTH_LONG).show()
            onRegisterClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueLight)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Judul
            Text(
                text = "Create Account",
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create an account so you can explore all the existing jobs",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F6FC),
                    unfocusedContainerColor = Color(0xFFF3F6FC),
                    focusedIndicatorColor = BluePrimary,
                    unfocusedIndicatorColor = Color(0xFFB0C4DE)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F6FC),
                    unfocusedContainerColor = Color(0xFFF3F6FC),
                    focusedIndicatorColor = BluePrimary,
                    unfocusedIndicatorColor = Color(0xFFB0C4DE)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F6FC),
                    unfocusedContainerColor = Color(0xFFF3F6FC),
                    focusedIndicatorColor = BluePrimary,
                    unfocusedIndicatorColor = Color(0xFFB0C4DE)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            
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

            // Tombol Sign Up
            Button(
                onClick = {
                    viewModel.register(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword
                    ) {
                        // onSuccess handled by LaunchedEffect
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sudah punya akun
            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Already have an account",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
