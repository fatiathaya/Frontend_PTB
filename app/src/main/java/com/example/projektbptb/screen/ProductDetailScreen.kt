package com.example.projektbptb.screen

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.example.projektbptb.R
import com.example.projektbptb.model.Comment
import com.example.projektbptb.model.ProductDetail
import com.example.projektbptb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductDetail = ProductDetail(
        name = "Beruang",
        category = "Perabotan",
        condition = "Baru",
        price = "Rp 50.000",
        description = "Beruang dengan desain modern, cocok untuk kamar kos. Kondisi masih baru, belum pernah dipakai.",
        location = "Jl. Sudirman No. 123, Jakarta",
        whatsappNumber = "081234567890",
        imageRes = R.drawable.teddy
    ),
    onBackClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {}
) {
    var commentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(product.comments) }
    var isFavorite by remember { mutableStateOf(product.isFavorite) }
    var showCommentInput by remember { mutableStateOf(false) }
    var commentTextFieldFocusRequester = remember { FocusRequester() }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editingCommentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Produk",
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
                actions = {
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick()
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) RedPrimary else GrayDark
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
            // Gambar Produk
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nama Produk
                Text(
                    text = product.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                // Kategori dan Kondisi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = BlueLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 12.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        color = GrayLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = product.condition,
                            fontSize = 12.sp,
                            color = GrayDark,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Harga
                Text(
                    text = product.price,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )

                Divider(color = GrayLight, thickness = 1.dp)

                // Deskripsi
                Column {
                    Text(
                        text = "Deskripsi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = product.description,
                        fontSize = 14.sp,
                        color = GrayDark,
                        lineHeight = 20.sp
                    )
                }

                Divider(color = GrayLight, thickness = 1.dp)

                // Lokasi
                Column {
                    Text(
                        text = "Lokasi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = product.location,
                        fontSize = 14.sp,
                        color = GrayDark
                    )
                }

                Divider(color = GrayLight, thickness = 1.dp)

                // Seller Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(BlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = product.sellerName.first().toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        }
                        Column {
                            Text(
                                text = product.sellerName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Black
                            )
                            Text(
                                text = "Penjual",
                                fontSize = 12.sp,
                                color = GrayDark
                            )
                        }
                    }
                    Button(
                        onClick = onWhatsAppClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Hubungi",
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Divider(color = GrayLight, thickness = 1.dp)

                // Komentar Section
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCommentInput = true }
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Komentar (${comments.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        if (!showCommentInput) {
                            Text(
                                text = "Tulis komentar",
                                fontSize = 14.sp,
                                color = BluePrimary
                            )
                        }
                    }

                    // Input Komentar (muncul hanya ketika showCommentInput = true)
                    if (showCommentInput) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("Tulis komentar...", color = GrayDark) },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(commentTextFieldFocusRequester),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = BluePrimary,
                                    unfocusedIndicatorColor = GrayLight,
                                    focusedContainerColor = White,
                                    unfocusedContainerColor = White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                maxLines = 3
                            )
                            IconButton(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        val newComment = Comment(
                                            id = System.currentTimeMillis().toString(),
                                            userName = "Anda",
                                            commentText = commentText.trim(),
                                            timestamp = "Baru saja"
                                        )
                                        // Tambahkan komentar baru ke list (langsung tampil)
                                        comments = comments + newComment
                                        // Kosongkan input
                                        commentText = ""
                                        // Sembunyikan input setelah komentar ditambahkan
                                        showCommentInput = false
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(BluePrimary, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Auto focus ketika input muncul
                        LaunchedEffect(showCommentInput) {
                            if (showCommentInput) {
                                commentTextFieldFocusRequester.requestFocus()
                            }
                        }
                    }

                    // Daftar Komentar - Tampilkan semua komentar yang sudah dibuat
                    if (comments.isEmpty()) {
                        // Jika belum ada komentar
                        Text(
                            text = "Belum ada komentar. Jadilah yang pertama berkomentar!",
                            fontSize = 14.sp,
                            color = GrayDark,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        // Tampilkan semua komentar yang sudah dibuat (langsung tampil setelah ditambahkan)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = if (showCommentInput) 0.dp else 8.dp)
                        ) {
                            comments.forEach { comment ->
                                CommentItem(
                                    comment = comment,
                                    isEditing = editingCommentId == comment.id,
                                    editingText = if (editingCommentId == comment.id) editingCommentText else "",
                                    onEditTextChange = { editingCommentText = it },
                                    onEditClick = {
                                        editingCommentId = comment.id
                                        editingCommentText = comment.commentText
                                    },
                                    onSaveEdit = {
                                        comments = comments.map { c ->
                                            if (c.id == comment.id) {
                                                c.copy(commentText = editingCommentText.trim())
                                            } else {
                                                c
                                            }
                                        }
                                        editingCommentId = null
                                        editingCommentText = ""
                                    },
                                    onCancelEdit = {
                                        editingCommentId = null
                                        editingCommentText = ""
                                    },
                                    onDeleteClick = {
                                        comments = comments.filter { it.id != comment.id }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isEditing: Boolean = false,
    editingText: String = "",
    onEditTextChange: (String) -> Unit = {},
    onEditClick: () -> Unit = {},
    onSaveEdit: () -> Unit = {},
    onCancelEdit: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val isOwnComment = comment.userName == "Anda"
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.userName.first().toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.timestamp,
                            fontSize = 12.sp,
                            color = GrayDark
                        )
                        // Tombol Edit dan Delete hanya untuk komentar sendiri
                        if (isOwnComment && !isEditing) {
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                if (isEditing) {
                    // Mode Edit: Tampilkan TextField
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editingText,
                            onValueChange = onEditTextChange,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = BluePrimary,
                                unfocusedIndicatorColor = GrayLight,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 3
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onCancelEdit) {
                                Text("Batal", color = GrayDark)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onSaveEdit,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Simpan", color = White)
                            }
                        }
                    }
                } else {
                    // Mode Normal: Tampilkan teks komentar
                    Text(
                        text = comment.commentText,
                        fontSize = 14.sp,
                        color = Black,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

