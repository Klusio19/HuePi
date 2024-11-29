package com.klusio19.huepi.presentation.screens.light_details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LightDetailsScreen(rid: String) {
    Text(modifier = Modifier.fillMaxSize(), text = "LightDetailScreen with ID = \"${rid}\"")
}