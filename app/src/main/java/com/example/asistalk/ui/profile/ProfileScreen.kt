package com.example.asistalk.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.asistalk.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first
import com.example.asistalk.utils.UserPreferencesRepository
import androidx.compose.runtime.*
import coil.request.ImageRequest

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val uiState = profileViewModel.uiState
    val userPrefs = remember { UserPreferencesRepository(context) }

    // Data cadangan dari lokal
    val localProfileImageUrl by userPrefs.profileImageFlow.collectAsState("")

    LaunchedEffect(Unit) {
        val userId = userPrefs.userIdFlow.first()

        if (userId != -1) {
            profileViewModel.fetchProfile(userId)
        } else {
            val fullname = userPrefs.fullnameFlow.first()
            val username = userPrefs.usernameFlow.first()
            val email = userPrefs.emailFlow.first()
            profileViewModel.loadProfile(fullname, email, username)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Text(
            text = "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    when {
                        uiState.profileImageUri != null -> uiState.profileImageUri
                        localProfileImageUrl.isNotEmpty() -> localProfileImageUrl
                        else -> R.drawable.logo_asistalk_hijau
                    }
                )
                .crossfade(true)
                .crossfade(500)
                .placeholder(R.drawable.logo_asistalk_hijau)
                .error(R.drawable.logo_asistalk_hijau)
                .build(),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = uiState.fullName.ifEmpty { "User Name" },
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            text = uiState.email.ifEmpty { "user@email.com" },
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileMenuItem(
            text = "Your Profile",
            icon = Icons.Default.AccountCircle,
            onClick = { navController.navigate("yourProfile") }
        )

        ProfileMenuItem(
            text = "Logout",
            icon = Icons.Default.ExitToApp,
            onClick = {
            },
            textColor = Color.Red,
            iconColor = Color.Red
        )
    }
}

@Composable
private fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    textColor: Color = LocalContentColor.current,
    iconColor: Color = LocalContentColor.current
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = textColor
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        navController = rememberNavController(),
        profileViewModel = viewModel()
    )
}
