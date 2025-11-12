package com.example.projektbptb.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projektbptb.ui.component.BottomNavigationBar
import com.example.projektbptb.ui.theme.BluePrimary
import com.example.projektbptb.ui.theme.White
import com.example.projektbptb.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToSell: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    val user = viewModel.user.value
    val notifEnabled by viewModel.isNotificationEnabled

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Setting",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
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
        },
        bottomBar = { 
            BottomNavigationBar(
                currentRoute = "settings",
                onNavigateToHome = onNavigateToHome,
                onNavigateToSell = onNavigateToSell,
                onNavigateToSettings = {}
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(25.dp))

            // ✅ Menu Items
            SettingItem("Alamat", onClick = onNavigateToAddress)
            SettingItem("Bahasa", onClick = onNavigateToLanguage)
            SettingItem("Ganti Password", onClick = onNavigateToChangePassword)

            // ✅ Notification Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF6F6F6))
                    .padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notifikasi")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = notifEnabled,
                    onCheckedChange = { viewModel.toggleNotification() }
                )
            }

            SettingItem("Pusat Bantuan", onClick = onNavigateToHelpCenter)
            SettingItem("Keluar", textColor = Color.Red)

            Spacer(Modifier.height(60.dp)) // ✅ Aman dari bottom bar
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF6F6F6))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = textColor, fontSize = 15.sp)
        Spacer(Modifier.weight(1f))
        Text(">", fontSize = 20.sp)
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(viewModel = SettingsViewModel()) // ✅ Preview benar tanpa warning
}
