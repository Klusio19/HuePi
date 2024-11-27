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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.klusio19.huepi.model.LightBulb
import com.klusio19.huepi.presentation.components.LightBulbItem
import com.klusio19.huepi.ui.theme.HuePiTheme


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


@Preview(showSystemUi = true)
@Composable
private fun HomeContentPreview() {
    HuePiTheme {
        HomeContent(
            lightBulbsList = lightBulbsList,
            onLightBulbClicked = {},
        )
    }
}

val lightBulbsList = listOf(
    LightBulb(
    rid = "xd2137",
    brightness = 100.0F,
    color = "#ffe600",
    isOn = true,
    name = "Lampka dupa",
    taskRunning = true
), LightBulb(
    rid = "asfjhl",
    brightness = 45.0F,
    color = "#221336",
    isOn = true,
    name = "Ciemnofioletowa lampka z bardzo długaśną nazwą która się rozjedzie na cały komponent i będzie bardzo śmiesznie",
    taskRunning = false
),
    LightBulb(
        rid = "45689utud",
        brightness = 69.0F,
        color = "#cf216c",
        isOn = false,
        name = "Wyłączona lampka",
        taskRunning = true
    )
)
