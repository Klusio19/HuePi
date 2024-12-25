package com.klusio19.huepi.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.klusio19.huepi.model.LightBulb
import com.klusio19.huepi.presentation.components.LightBulbItem


@Composable
fun HomeContent(
    lightBulbsList: List<LightBulb>?,
    onLightBulbClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (lightBulbsList.isNullOrEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = "No light bulbs found!",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lightBulbsList) { lightBulb ->
                LightBulbItem(
                    color = Color(lightBulb.color.toColorInt()),
                    lightBulbName = lightBulb.name,
                    brightnessLevel = lightBulb.brightness,
                    lightBulbOn = lightBulb.isOn,
                    rid = lightBulb.rid,
                    onLightBulbClicked = onLightBulbClicked
                )
            }
        }
    }
}