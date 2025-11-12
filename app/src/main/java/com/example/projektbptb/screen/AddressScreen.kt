package com.example.projektbptb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projektbptb.data.model.Address
import com.example.projektbptb.ui.theme.*
import com.example.projektbptb.viewmodel.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onAddressClick: (Address) -> Unit = {},
    onAddAddressClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf<Address?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Alamat",
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
        },
        bottomBar = {
            Button(
                onClick = onAddAddressClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                ),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Tambah alamat",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
                .padding(bottom = 72.dp), // Space for bottom button
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Cari alamat", color = GrayDark) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = GrayDark)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = GrayLight,
                    unfocusedIndicatorColor = GrayLight,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.LocationOn,
                    text = "Lokasi saat ini",
                    onClick = { /* TODO: Get current location */ },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Default.LocationOn,
                    text = "Pilih lewat peta",
                    onClick = onAddAddressClick,
                    modifier = Modifier.weight(1f)
                )
            }

            // Saved Addresses Section
            Text(
                "Alamat Tersimpan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )

            if (viewModel.savedAddresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Belum ada alamat tersimpan",
                        fontSize = 14.sp,
                        color = GrayDark
                    )
                }
            } else {
                viewModel.savedAddresses.forEach { address ->
                    AddressItem(
                        address = address,
                        onClick = { onAddressClick(address) },
                        onMenuClick = { showMenu = address }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Menu for address options
            showMenu?.let { address ->
                Box {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showMenu = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = null
                                onAddressClick(address)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = RedPrimary) },
                            onClick = {
                                showMenu = null
                                viewModel.deleteAddress(address)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = RedPrimary)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .border(1.dp, GrayLight, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = GrayDark
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(
                text,
                fontSize = 14.sp,
                color = GrayDark
            )
        }
    }
}

@Composable
fun AddressItem(
    address: Address,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, GrayLight, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = null,
                tint = GrayDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    address.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Text(
                    address.fullAddress,
                    fontSize = 12.sp,
                    color = GrayDark
                )
            }
            IconButton(onClick = onMenuClick) {
                Text(
                    "⋮",
                    fontSize = 24.sp,
                    color = GrayDark
                )
            }
        }
    }
}
