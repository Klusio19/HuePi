package com.klusio19.huepi.presentation.screens.light_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klusio19.huepi.model.LightBulb
import com.klusio19.huepi.ui.theme.HuePiTheme

@Composable
fun LightDetailsContent(
    lightBulb: LightBulb,
    modifier: Modifier = Modifier,
    onTurnOnSwitched: () -> Unit,
    onTurnOffSwitched: () -> Unit,
    onBrightnessSet: (Float) -> Unit
) {
    var isOnState by remember { mutableStateOf(lightBulb.isOn) }

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        PowerStateRow(
            isOn = isOnState,
            onCheckedChange = { isChecked ->
                isOnState = isChecked
                if (isChecked) {
                    onTurnOnSwitched()
                } else {
                    onTurnOffSwitched()
                }
            }
        )

        BrightnessSlider(
            brightnessLevel = lightBulb.brightness,
            onBrightnessSet = onBrightnessSet
        )

        Text("Rid: ${lightBulb.rid}")
        Text("Name: ${lightBulb.name}")
        Text("Brightness: ${lightBulb.brightness}")
        Text("Color: ${lightBulb.color}")
        Text("IsOn: ${lightBulb.isOn}")
        Text("TaskRunning: ${lightBulb.taskRunning}")
    }
}


@Composable
fun PowerStateRow(
    isOn: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("Power state")
        Switch(
            checked = isOn,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun BrightnessSlider(
    brightnessLevel: Float,
    onBrightnessSet: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(brightnessLevel) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it},
            onValueChangeFinished = {
                onBrightnessSet(sliderPosition)
            }
        )
        Text(text = "%.2f".format(sliderPosition))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LightDetailsContentPreview() {
    HuePiTheme(darkTheme = true) {
        LightDetailsContent(
            LightBulb(
                rid = "2137xd",
                brightness = 21.37F,
                color = "#213F",
                isOn = true,
                name = "Taktyczna nazwa żarówki",
                taskRunning = false
            ),
            modifier = Modifier,
            onTurnOnSwitched = {},
            onTurnOffSwitched = {},
            onBrightnessSet = {}
        )
    }
}
