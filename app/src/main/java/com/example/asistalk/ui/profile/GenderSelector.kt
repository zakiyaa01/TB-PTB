package com.example.asistalk.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenderSelector(
    selectedGender: String,
    onGenderSelect: (String) -> Unit
) {
    Column {
        Text("Gender")
        Spacer(Modifier.height(8.dp))

        Row {
            listOf("Male", "Female").forEach { gender ->
                FilterChip(
                    selected = selectedGender == gender,
                    onClick = { onGenderSelect(gender) },
                    label = { Text(gender) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
