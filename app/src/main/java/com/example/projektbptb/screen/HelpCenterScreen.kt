package com.example.projektbptb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projektbptb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pusat Bantuan",
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
            // FAQ Section
            Text(
                "Pertanyaan Umum",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HelpItem(
                title = "Bagaimana cara menambah produk?",
                onClick = { /* TODO: Show FAQ detail */ }
            )
            HelpItem(
                title = "Bagaimana cara menghubungi penjual?",
                onClick = { /* TODO: Show FAQ detail */ }
            )
            HelpItem(
                title = "Bagaimana cara mengubah profil?",
                onClick = { /* TODO: Show FAQ detail */ }
            )
            HelpItem(
                title = "Bagaimana cara menghapus akun?",
                onClick = { /* TODO: Show FAQ detail */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Section
            Text(
                "Hubungi Kami",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ContactItem(
                icon = Icons.Default.Email,
                title = "Email",
                subtitle = "support@projektbptb.com",
                onClick = { /* TODO: Open email */ }
            )
            ContactItem(
                icon = Icons.Default.Phone,
                title = "Telepon",
                subtitle = "+62 812-3456-7890",
                onClick = { /* TODO: Open phone */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            Text(
                "Tentang Aplikasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                "Versi: 1.0.0",
                fontSize = 14.sp,
                color = GrayDark
            )
            Text(
                "Aplikasi untuk jual beli produk bekas",
                fontSize = 14.sp,
                color = GrayDark
            )
        }
    }
}

@Composable
fun HelpItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GrayLight)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 15.sp,
            color = Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = null,
            tint = GrayDark,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GrayLight)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = BluePrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )
            Text(
                subtitle,
                fontSize = 13.sp,
                color = GrayDark
            )
        }
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = null,
            tint = GrayDark,
            modifier = Modifier.size(20.dp)
        )
    }
}

