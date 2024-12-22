package com.klusio19.huepi.presentation.screens.light_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.klusio19.huepi.model.LightBulb

@Composable
fun LightDetailsContent(
    lightBulb: LightBulb,
    modifier: Modifier = Modifier,
    onTurnOnSwitched: () -> Unit,
    onTurnOffSwitched: () -> Unit,
    onBrightnessSet: (Float) -> Unit,
    onColorChosen: (Float, Float, Float) -> Unit
) {
    var isOnState by remember { mutableStateOf(lightBulb.isOn) }
    val colorController = rememberColorPickerController()
    var initialColor = Color(lightBulb.color.toColorInt())

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

        BrightnessSliderWithText(
            brightnessLevel = lightBulb.brightness,
            onBrightnessSet = onBrightnessSet
        )

        ColorPickerWithTileAndButton(
            colorController = colorController,
            initialColor = initialColor,
            onColorChosen = onColorChosen
        )
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Power state")
        Switch(
            checked = isOn,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun BrightnessSliderWithText(
    brightnessLevel: Float,
    onBrightnessSet: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(brightnessLevel) }

    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it},
            onValueChangeFinished = {
                onBrightnessSet(sliderPosition)
            }
        )
        Text(text = "Brightness: %.2f%%".format(sliderPosition * 100))
    }
}

@Composable
fun ColorPickerWithTileAndButton(
    colorController: ColorPickerController,
    initialColor: Color,
    onColorChosen: (Float, Float, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AlphaTile(
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = colorController
        )
        Spacer(modifier = Modifier.height(16.dp))
        ColorPickerWheel(
            colorController = colorController,
            initialColor = initialColor,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val rgbValues = Triple(
                    colorController.selectedColor.value.red,
                    colorController.selectedColor.value.green,
                    colorController.selectedColor.value.blue
                )
                val hsvValues = rgbToHsv(
                    rgbValues.first,
                    rgbValues.second,
                    rgbValues.third
                )
                onColorChosen(
                    hsvValues.first,
                    hsvValues.second,
                    hsvValues.third
                )
            }
        ) {
            Text("Set color")
        }
    }
}

@Composable
fun ColorPickerWheel(
    colorController: ColorPickerController,
    initialColor: Color
) {
    HsvColorPicker(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F),
        controller = colorController,
        initialColor = initialColor
    )

}

fun rgbToHsv(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
    val r255 = (r * 255).toInt()
    val g255 = (g * 255).toInt()
    val b255 = (b * 255).toInt()

    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(r255, g255, b255, hsv)

    val h = hsv[0] / 360f
    val s = hsv[1]
    val v = hsv[2]

    return Triple(h, s, v)
}