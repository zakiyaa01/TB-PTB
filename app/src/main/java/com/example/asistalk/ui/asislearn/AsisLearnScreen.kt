package com.example.asistalk.ui.asislearn

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asistalk.network.MaterialItem
import com.example.asistalk.utils.UserPreferencesRepository
import kotlinx.coroutines.flow.first

@Composable
fun AsisLearnScreen(
    navController: NavHostController,
    viewModel: AsisLearnViewModel
) {
    // 1. Ambil Context di level Screen utama
    val context = LocalContext.current
    val userPrefsRepo = remember { UserPreferencesRepository(context) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "My Material", "Download")
    var searchQuery by remember { mutableStateOf("") }

    val materials by viewModel.materials.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Ambil Session & Fetch Materi Pertama Kali
    LaunchedEffect(Unit) {
        val id = userPrefsRepo.userIdFlow.first()
        val fullName = userPrefsRepo.fullnameFlow.first()

        viewModel.setSession(id, fullName)
        viewModel.fetchAllMaterials()
    }

    // Filter reaktif & Cek Download jika tab Download diklik
    LaunchedEffect(selectedTab, searchQuery, materials) {
        viewModel.selectedTabIndex = selectedTab
        viewModel.searchQuery = searchQuery

        // Pemicu pengecekan file lokal jika masuk ke tab Download
        if (selectedTab == 2) {
            viewModel.checkDownloadedMaterials(context, materials)
        }

        viewModel.filterMaterials()
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // HEADER: SEARCH + UPLOAD
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari subjek atau topik...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.resetInputStates()
                        navController.navigate("uploadMaterial")
                    },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Upload", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // TAB NAVIGATION
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // LIST CONTENT
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                }
                materials.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Materi tidak ditemukan", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(materials, key = { it.id }) { item ->
                            // Kirim context ke MaterialCard
                            MaterialCard(item, navController, viewModel, context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialCard(
    item: MaterialItem,
    navController: NavHostController,
    viewModel: AsisLearnViewModel,
    context: Context // 2. Tambahkan parameter context di sini
) {
    val isLoading by viewModel.isLoading.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isMyMaterial = item.user_id == viewModel.currentUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.file_type.uppercase()) {
                            "PDF" -> Icons.Default.PictureAsPdf
                            "VIDEO" -> Icons.Default.PlayCircle
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.subject, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(item.topic, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("detailMaterial/${item.id}") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lihat Materi", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = Color.LightGray)
                    Spacer(Modifier.width(4.dp))
                    Text(item.author_name, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }

                // TOMBOL DOWNLOAD
                IconButton(onClick = {
                    // Gunakan context yang dikirim dari Screen utama
                    viewModel.downloadMaterial(
                        context = context,
                        url = item.file_path,
                        subject = item.subject
                    )
                }) {
                    Icon(Icons.Default.Download, null, tint = MaterialTheme.colorScheme.primary)
                }

                if (isMyMaterial) {
                    // TOMBOL EDIT
                    IconButton(onClick = {
                        viewModel.setEditData(item)
                        navController.navigate("editMaterial")
                    }) {
                        Icon(Icons.Default.Edit, null, tint = Color.Gray)
                    }

                    // TOMBOL HAPUS
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                    }
                }
            }
        }
    }

    // DIALOG KONFIRMASI HAPUS
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Materi?") },
            text = { Text("Apakah Anda yakin ingin menghapus materi '${item.subject}' ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMaterial(item.id)
                        showDeleteDialog = false
                    }
                ) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}