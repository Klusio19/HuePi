package com.klusio19.huepi.presentation.screens.light_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.window.Dialog
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
    var colorPickerDialogOpened by remember { mutableStateOf(false) }
    val controller = rememberColorPickerController()


    if (colorPickerDialogOpened) {
        ColorPickerDialog(
            onDialogDismissed = { colorPickerDialogOpened = false },
            colorController = controller,
            onColorChosen = onColorChosen,
            initialColor = Color(lightBulb.color.toColorInt())
        )
    }

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

        Button(
            onClick = {colorPickerDialogOpened = true}
        ) {
            Text("Color")
        }
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
            .padding(bottom = 10.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    colorController: ColorPickerController,
    onDialogDismissed: () -> Unit,
    onColorChosen: (Float, Float, Float) -> Unit,
    initialColor: Color
) {
    Dialog(
        onDismissRequest = onDialogDismissed
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = colorController
                )
                ColorPicker(
                    colorController = colorController,
                    initialColor = initialColor
                )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = onDialogDismissed
                    ) {
                        Text("Close")
                    }
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
                            onDialogDismissed()
                        }
                    ) {
                        Text("Set & close")
                    }
                }

            }

        }
    }
}

@Composable
fun ColorPicker(
    colorController: ColorPickerController,
    initialColor: Color
) {
    HsvColorPicker(
        modifier = Modifier.size(300.dp),
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

    val h = hsv[0] / 360f  // Hue ranges from 0 to 360
    val s = hsv[1]        // Saturation is already in 0 to 1 range
    val v = hsv[2]        // Value is already in 0 to 1 range

    return Triple(h, s, v)
}