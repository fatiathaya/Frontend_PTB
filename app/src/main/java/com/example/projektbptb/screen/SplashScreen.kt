package com.example.projektbptb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLanding: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(2000) // Tampilkan splash selama 2 detik
        onNavigateToLanding()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF99D0FF)), // Biru muda latar belakang
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SPLG",
                color = Color(0xFF0029B7), // Biru tua
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    LandingScreen()
}