package com.example.asistalk.ui.asislearn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.asistalk.R

@Composable
fun AsisLearnScreen(navController: NavHostController) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "My Material", "Download")

    val materials = listOf(
        MaterialItem("Modul 5 Praktikum PTB", "PDF", "Alexander", R.drawable.ic_pdf),
        MaterialItem("Modul 5 Praktikum PTB", "Video", "Alexander", R.drawable.ic_video),
        MaterialItem("Modul 5 Praktikum PTB", "PDF", "Alexander", R.drawable.ic_image),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search + Upload Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search...") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.width(10.dp))

            Button(
                onClick = { navController.navigate("uploadMaterial") },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Upload Material")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // List materials
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(materials) { item ->
                MaterialCard(item)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

data class MaterialItem(
    val title: String,
    val type: String,
    val author: String,
    val icon: Int
)

@Composable
fun MaterialCard(item: MaterialItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFD2EEF7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.type,
                    tint = Color(0xFF0081C9),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Modul Pertemuan 5: Material Design",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = item.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = item.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Buttons
            Column(horizontalAlignment = Alignment.End) {
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(12.dp)) {
                    Text("Lihat")
                }

                Spacer(Modifier.height(8.dp))

                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = "Download"
                    )
                }
            }
        }
    }
}