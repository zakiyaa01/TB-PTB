package com.example.asistalk.ui.asishub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.asistalk.R
import com.example.asistalk.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    vm: AsisHubViewModel
) {
    val post by vm.postToView.collectAsState(initial = null)
    var commentText by remember { mutableStateOf("") }
    var commentToDelete by remember { mutableStateOf<Comment?>(null) }
    var commentToEdit by remember { mutableStateOf<Comment?>(null) }

    var showDeletePostDialog by remember { mutableStateOf(false) }

    LaunchedEffect(post?.id) {
        post?.let { vm.loadComments(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postingan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            CommentInputSection(
                value = commentText,
                onValueChange = { commentText = it },
                onSendClick = {
                    post?.let {
                        vm.addComment(
                            postId = it.id,
                            content = commentText
                        )
                        commentText = ""
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            post?.let { currentPost ->
                item {
                    PostCard(
                        post = currentPost,
                        onClickPost = {},
                        onClickEdit = {
                            vm.selectPostForEditing(currentPost)
                            navController.navigate("editPost")
                        },
                        onClickDelete = {
                            showDeletePostDialog = true
                        }
                    )

                    Text(
                        "Komentar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (currentPost.comments.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada komentar", color = Color.Gray)
                        }
                    }
                } else {
                    items(currentPost.comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onEditClick = { commentToEdit = comment },
                            onDeleteClick = { commentToDelete = comment }
                        )
                    }
                }
            }
        }
    }

    commentToEdit?.let { comment ->
        EditCommentDialog(
            comment = comment,
            onDismiss = { commentToEdit = null },
            onConfirm = { newText ->
                post?.let { currentPost ->
                    vm.updateComment(currentPost.id, comment.id, newText)
                }
                commentToEdit = null
            }
        )
    }

    commentToDelete?.let { comment ->
        AlertDialog(
            onDismissRequest = { commentToDelete = null },
            title = { Text("Hapus Komentar") },
            text = { Text("Yakin ingin menghapus komentar ini?") },
            confirmButton = {
                TextButton(onClick = {
                    post?.let { vm.deleteComment(it.id, comment.id) }
                    commentToDelete = null
                }) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { commentToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDeletePostDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePostDialog = false },
            title = { Text("Hapus Postingan") },
            text = { Text("Apakah Anda yakin ingin menghapus postingan ini?") },
            confirmButton = {
                TextButton(onClick = {
                    post?.let {
                        vm.deletePost(it.id)
                        showDeletePostDialog = false
                        navController.popBackStack() // Kembali ke home setelah hapus
                    }
                }) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePostDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        AsyncImage(
            model = if (!comment.profileImage.isNullOrEmpty()) {
                "http://10.0.2.2:3000${comment.profileImage}" // Tambahkan URL Server
            } else {
                R.drawable.logo_asistalk_hijau
            },
            contentDescription = "Profile",
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Gray),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.logo_asistalk_hijau)
        )

        Spacer(Modifier.width(12.dp))

        Column(
            Modifier
                .weight(1f)
                .background(Color(0xFFF0F2F5), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(comment.author, fontWeight = FontWeight.Bold)
            Text(comment.content)
            Text(comment.timestamp, fontSize = 10.sp, color = Color.Gray)
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Opsi Komentar")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        expanded = false
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Hapus", color = Color.Red) },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

@Composable
fun CommentInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Tulis komentar...") },
            shape = RoundedCornerShape(24.dp)
        )
        IconButton(onClick = onSendClick, enabled = value.isNotBlank()) {
            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFF00BFA6))
        }
    }
}
@Composable
fun EditCommentDialog(
    comment: Comment,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(comment.content) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Komentar") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }) },
        confirmButton = { TextButton(onClick = { onConfirm(text) }) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}