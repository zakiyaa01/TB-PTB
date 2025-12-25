package com.example.asistalk.ui.asislearn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationAsisLearnScreen(navController: NavController) {
    val context = LocalContext.current
    val notifications = remember { NotificationHelper.getLogs(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aktivitas Materi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("Belum ada aktivitas materi", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF8FAFC))
            ) {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(notif.title, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2098D1), fontSize = 15.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(notif.message, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(notif.time)),
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}