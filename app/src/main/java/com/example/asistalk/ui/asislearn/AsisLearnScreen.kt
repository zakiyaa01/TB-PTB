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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.SolidColor

@Composable
fun AsisLearnScreen(
    navController: NavHostController,
    viewModel: AsisLearnViewModel
) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "My Material", "Download")
    var searchQuery by remember { mutableStateOf("") }

    // Efek Samping: Panggil fungsi filter di ViewModel setiap kali
    // searchQuery ATAU selectedTab berubah.
    LaunchedEffect(selectedTab, searchQuery) {
        viewModel.filterMaterials(searchQuery, selectedTab)
    }

    // Ambil data materi dari ViewModel (sudah terfilter)
    val materials by viewModel.materials.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search + Upload Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Hapus: viewModel.searchQuery(it)
                    // Fungsi filter sudah dihandle oleh LaunchedEffect
                },
                placeholder = { Text("Search...") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(Modifier.width(10.dp))

            Button(
                onClick = { navController.navigate("uploadMaterial") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "+ Upload Material",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            // Latar belakang TabRow dibuat netral (abu-abu muda/surface variant)
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            // Warna konten default diatur agar kontras di atas surfaceVariant
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicator = {
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(it[selectedTab]),
                    // Indikator tetap sekunder (Secondary)
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        selectedTab = index
                    },
                    text = {
                        Text(
                            title,
                            // Teks Tab Aktif dibuat menggunakan warna Primary
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // List materials (secara reaktif menampilkan hasil filter)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(materials) { item ->
                MaterialCard(item, navController, viewModel)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MaterialCard(
    item: MaterialItem,
    navController: NavHostController,
    viewModel: AsisLearnViewModel
) {
    val isMyMaterial = item.author == "Anda"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.type,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = item.topic.ifBlank { "Tidak ada topik" }, // Gunakan Topic dari ViewModel
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                // Baris Author dan Type
                Row {
                    Text(
                        text = item.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " · ${item.type}", // Tambahkan pemisah
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }

            // ========= BUTTON AREA (Fixed & Clean) =========
            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tombol LIHAT
                OutlinedButton(
                    onClick = {
                        viewModel.getMaterialByTitle(item.title)
                        val encoded = java.net.URLEncoder.encode(item.title, "UTF-8")
                        navController.navigate("materialDetail/$encoded")
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MaterialTheme.colorScheme.primary)
                    )
                ) {
                    Text("Lihat", style = MaterialTheme.typography.labelLarge)
                }

                // Row ikon (Download, Edit, Hapus)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // DOWNLOAD (Selalu ada)
                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = "Download",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isMyMaterial) {

                        // EDIT
                        IconButton(
                            onClick = { /* Navigate Edit */ },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // DELETE
                        IconButton(
                            onClick = { viewModel.deleteMaterial(item) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Hapus",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}