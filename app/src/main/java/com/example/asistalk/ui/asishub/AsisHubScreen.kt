package com.example.asistalk.ui.asishub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asistalk.R
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

// =============================================================
// MAIN SCREEN – ASISHUB
// =============================================================
@Composable
fun AsisHubScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---------------- HEADER ----------------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "AsisHub",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2098D1)
            )
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.ic_notification),
                contentDescription = "Notif",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        navController.navigate("notif")
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


        // ---------------- INPUT BOX ----------------
        PostInputBox(onClick = {
            navController.navigate("createPost")
        })

        Spacer(modifier = Modifier.height(16.dp))


        // ---------------- POST LIST ----------------
        PostCard(
            name = "Zakiya Aulia",
            time = "2 hours ago",
            content = "Tutorial lengkap instalasi Ubuntu Server…",
            comments = 2,
            hasMedia = true,
            onClickPost = {
                navController.navigate("postDetail")
            },
            onClickEdit = {
                navController.navigate("editPost")
            },
            onClickDelete = {
                navController.navigate("deletePost")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PostCard(
            name = "Suci Nurhaliza",
            time = "5 hours ago",
            content = "Halo semuanya! Ada yang tau cara mengatasi error…",
            comments = 1,
            hasMedia = false,
            onClickPost = {
                navController.navigate("postDetail")
            },
            onClickEdit = {
                navController.navigate("editPost")
            },
            onClickDelete = {
                navController.navigate("deletePost")
            }
        )
    }
}

// =============================================================
// CREATE POST SCREEN
// =============================================================
@Composable
fun CreatePostScreen(navController: NavHostController) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Create New Post", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Apa yang ingin anda diskusikan?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = {}) { Text("📷 Photo") }
            Spacer(Modifier.width(10.dp))
            Button(onClick = {}) { Text("🎬 Video") }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Posting")
        }
    }
}

// =============================================================
// NOTIFICATION SCREEN
// =============================================================
@Composable
fun NotificationScreen(navController: NavHostController) {
    Column(Modifier.padding(16.dp)) {
        Text("Comment Notifications", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        NotificationItem("Zakiya Aulia commented on your post")
        NotificationItem("Suci Nurhaliza replied to your comment")
        NotificationItem("Ahmad Fauzi replied to your comment")
        NotificationItem("Bambang Astro commented on your post")
    }
}

@Composable
fun NotificationItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5FF))
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            fontSize = 15.sp
        )
    }
}

// =============================================================
// INPUT POST BOX
// =============================================================
@Composable
fun PostInputBox(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color(0xFFDBF3FF), RoundedCornerShape(20.dp))
            .padding(14.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A57E8)),
            contentAlignment = Alignment.Center
        ) {
            Text("Z", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .background(Color(0xFFEAEAEA), RoundedCornerShape(20.dp))
                .padding(start = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Apa yang ingin anda diskusikan?", color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = "Camera",
            tint = Color(0xFF00BFA5)
        )
    }
}

// =============================================================
// POST CARD
// =============================================================
@Composable
fun PostCard(
    name: String,
    time: String,
    content: String,
    comments: Int,
    hasMedia: Boolean,
    onClickPost: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickPost() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ------- HEADER -------
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A57E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text(time, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.weight(1f))

                DropdownMenuButton(
                    onEdit = onClickEdit,
                    onDelete = onClickDelete
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(content, fontSize = 14.sp)

            // ------- MEDIA -------
            if (hasMedia) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFFE5E5E5), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_video),
                        contentDescription = "Video",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(thickness = 1.dp, color = Color(0xFFEAEAEA))

            Text(
                "${comments} Comments",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}

// =============================================================
// DROP DOWN MENU
// =============================================================
@Composable
fun DropdownMenuButton(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "Menu",
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Post") },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete Post") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}
