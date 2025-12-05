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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    vm: AsisHubViewModel
) {
    val post by vm.postToView.collectAsState()
    var commentText by remember { mutableStateOf("") }

    var commentToDelete by remember { mutableStateOf<Comment?>(null) }
    var commentToEdit by remember { mutableStateOf<Comment?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Postingan") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                )
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        },
        bottomBar = {
            CommentInputSection(
                value = commentText,
                onValueChange = { commentText = it },
                onSendClick = {
                    if (commentText.isNotBlank()) {
                        post?.let {
                            vm.addComment(it.id, commentText)
                        }
                        commentText = ""
                    }
                }
            )
        }
    ) { innerPadding ->
        commentToDelete?.let { comment ->
            post?.let { p ->
                AlertDialog(
                    onDismissRequest = { commentToDelete = null },
                    title = { Text("Hapus Komentar") },
                    text = { Text("Anda yakin ingin menghapus komentar ini?") },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.deleteComment(p.id, comment.id)
                            commentToDelete = null
                        }) { Text("Hapus") }
                    },
                    dismissButton = {
                        TextButton(onClick = { commentToDelete = null }) { Text("Batal") }
                    }
                )
            }
        }

        commentToEdit?.let { comment ->
            post?.let { p ->
                EditCommentDialog(
                    comment = comment,
                    onDismiss = { commentToEdit = null },
                    onConfirm = { newContent ->
                        vm.updateComment(p.id, comment.id, newContent)
                        commentToEdit = null
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        onClickDelete = {}
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Komentar",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 18.sp
                    )
                }

                if (currentPost.comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada komentar.", color = Color.Gray)
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
}

@Composable
fun CommentItem(
    comment: Comment,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.author.firstOrNull()?.toString()?.uppercase() ?: "U",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF0F2F5), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = comment.author, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.content)
        }

        Spacer(modifier = Modifier.width(4.dp))

        if (comment.author == "Zakiya Aulia") {
            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opsi komentar")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Edit") }, onClick = {
                        onEditClick()
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Hapus") }, onClick = {
                        onDeleteClick()
                        expanded = false
                    })
                }
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
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis komentar...") },
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Kirim Komentar",
                    tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun EditCommentDialog(
    comment: Comment,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(comment.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Komentar") },
        text = {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Komentar") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editedText.isNotBlank()) {
                        onConfirm(editedText)
                    }
                },
                enabled = editedText.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PostDetailScreenPreview() {
    // Data dummy untuk preview, tidak mempengaruhi aplikasi utama
    val dummyComment = Comment("1", "User Preview", "Ini contoh komentar.", "Baru saja")
    CommentItem(comment = dummyComment, onEditClick = {}, onDeleteClick = {})
}