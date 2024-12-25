package com.klusio19.huepi.presentation.screens.light_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.klusio19.huepi.R
import com.klusio19.huepi.model.LightBulb
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType
import android.graphics.Color as AndroidColor

@Composable
fun LightDetailsContent(
    lightBulb: LightBulb,
    modifier: Modifier = Modifier,
    onTurnOnSwitched: () -> Unit,
    onTurnOffSwitched: () -> Unit,
    onBrightnessSet: (brightness: Float) -> Unit,
    onColorChosen: (h: Float, s: Float, v: Float) -> Unit,
    onTempToColorTaskSet: (hueMin: Float, hueMax: Float, tempMin: Float, tempMax: Float) -> Unit,
    onStopTaskClicked: () -> Unit,
    refreshLightBulbData: () -> Unit
) {
    var isOnState by remember { mutableStateOf(lightBulb.isOn) }
    val colorController = rememberColorPickerController()
    var initialColor = Color(lightBulb.color.toColorInt())
    var tempToColorDialogOpened by remember { mutableStateOf(false) }
    var hueMinState by remember { mutableStateOf<Float?>(null) }
    var hueMaxState by remember { mutableStateOf<Float?>(null) }
    var tempMinState by remember { mutableStateOf("") }
    var tempMaxState by remember { mutableStateOf("") }

    if (tempToColorDialogOpened) {
        TempToColorDialog(
            onDialogDismissed = { tempToColorDialogOpened = false },
            onMinHueSet = { hue ->
                hueMinState = hue
            },
            onMaxHueSet = { hue ->
                hueMaxState = hue
            },
            hueMin = hueMinState,
            hueMax = hueMaxState,
            tempMin = tempMinState,
            tempMax = tempMaxState,
            onMinTempTextChange = { newText ->
                tempMinState = newText
            },
            onMaxTempTextChange = { newText ->
                tempMaxState = newText
            },
            taskRunning = lightBulb.taskRunning,
            onStartTaskClicked = {
                onTempToColorTaskSet(
                    hueMinState?.toFloat()!!,
                    hueMaxState?.toFloat()!!,
                    tempMinState.toFloat(),
                    tempMaxState.toFloat()
                )
            },
            onStopTaskClicked = onStopTaskClicked,
            refreshLightBulbData = refreshLightBulbData
        )
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
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
        Button(
            onClick = {tempToColorDialogOpened = true}
        ) {
            Text("Temp to color")
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TempToColorDialog(
    onDialogDismissed: () -> Unit,
    onMinHueSet: (Float) -> Unit,
    onMaxHueSet: (Float) -> Unit,
    hueMin: Float?,
    hueMax: Float?,
    tempMin: String,
    tempMax: String,
    onMinTempTextChange: (String) -> Unit,
    onMaxTempTextChange: (String) -> Unit,
    taskRunning: Boolean,
    onStartTaskClicked: () -> Unit,
    onStopTaskClicked: () -> Unit,
    refreshLightBulbData: () -> Unit
) {
    var selectedDialogColor by remember { mutableStateOf<Color>(Color.Red) }

    Dialog(
        onDismissRequest = onDialogDismissed
    ) {
        Card(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 6.dp),
                        painter = painterResource(R.drawable.baseline_info_outline_24),
                        contentDescription = ""
                    )
                    Text("The colors will be scaled as temperature, clockwise.")
                }

                ColorPicker(
                    type = ColorPickerType.Ring(
                        showLightnessBar = false,
                        showDarknessBar = false,
                        showAlphaBar = false,
                        showColorPreview = true
                    )
                ) { color ->
                    selectedDialogColor = color
                }
                Button(
                    onClick = {
                        val hsv = rgbToHsv(
                            selectedDialogColor.red,
                            selectedDialogColor.green,
                            selectedDialogColor.blue
                        )
                        onMinHueSet(hsv.first)
                    }
                ) {
                    Text("Set color for min temperature")
                }
                Button(
                    onClick = {
                        val hsv = rgbToHsv(
                            selectedDialogColor.red,
                            selectedDialogColor.green,
                            selectedDialogColor.blue
                        )
                        onMaxHueSet(hsv.first)
                    }
                ) {
                    Text("Set color for max temperature")
                }
                Column {
                    Text("Color for min temp (hue): ${hueMin?.let { "%.3f".format(it) } ?: "Not set"}")
                    Text("Color for max temp (hue): ${hueMax?.let { "%.3f".format(it) } ?: "Not set"}")
                }
                TextField(
                    value = tempMin,
                    singleLine = true,
                    onValueChange = { newText ->
                        onMinTempTextChange(newText)
                    },
                    label = { Text("Min temp:") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )
                TextField(
                    value = tempMax,
                    singleLine = true,
                    onValueChange = { newText ->
                        onMaxTempTextChange(newText)
                    },
                    label = { Text("Max temp:") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )
                Button(
                    onClick = {
                        onStartTaskClicked()
                        refreshLightBulbData()
                    },
                    enabled = (taskRunning == false) && (hueMin != null) && (hueMax != null) && (tempMin != "") && (tempMax != "")
                ) {
                    Text("Start displaying")
                }
                Button(
                    onClick = {
                        onStopTaskClicked()
                        refreshLightBulbData()
                    },
                    enabled = (taskRunning == true)
                ) {
                    Text("Stop displaying")
                }
            }
        }
    }
}

fun rgbToHsv(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
    val r255 = (r * 255).toInt()
    val g255 = (g * 255).toInt()
    val b255 = (b * 255).toInt()

    val hsv = FloatArray(3)
    AndroidColor.RGBToHSV(r255, g255, b255, hsv)

    val h = hsv[0] / 360f
    val s = hsv[1]
    val v = hsv[2]

    return Triple(h, s, v)
}

