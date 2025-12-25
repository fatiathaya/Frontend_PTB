package com.example.projektbptb.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projektbptb.ui.theme.*

@Composable
fun BottomNavigationBar(
    currentRoute: String = "home",
    isProfileComplete: Boolean = true,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSell: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCompleteProfile: () -> Unit = {}
) {
    var showIncompleteProfileDialog by remember { mutableStateOf(false) }
    
    if (showIncompleteProfileDialog) {
        AlertDialog(
            onDismissRequest = { showIncompleteProfileDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Profil Belum Lengkap",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Anda harus melengkapi profil terlebih dahulu untuk bisa menjual produk.",
                        fontSize = 14.sp
                    )
                    Text(
                        "Pastikan email dan nomor telepon sudah terisi.",
                        fontSize = 13.sp,
                        color = GrayDark
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showIncompleteProfileDialog = false
                        onNavigateToCompleteProfile()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary
                    )
                ) {
                    Text("Lengkapi Profil")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showIncompleteProfileDialog = false }
                ) {
                    Text("Nanti", color = GrayDark)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "sell",
            onClick = {
                if (isProfileComplete) {
                    onNavigateToSell()
                } else {
                    showIncompleteProfileDialog = true
                }
            },
            icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
            label = { Text("Sell") }
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}
