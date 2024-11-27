package com.klusio19.huepi.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klusio19.huepi.R
import com.klusio19.huepi.ui.theme.HuePiTheme


@Composable
fun LightBulbItem(
    color: Color,
    lightBulbName: String,
    brightnessLevel: Float,
    lightBulbOn: Boolean,
    rid: String,
    onLightBulbClicked: (String) -> Unit,
) {
    val usedColor = if (lightBulbOn) color else Color.Black
    val textColor = if (usedColor.luminance() > 0.5f) Color.Black else Color.White

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                3.dp,
                MaterialTheme.colorScheme.inversePrimary,
                shape = RoundedCornerShape(20.dp)
            )
            .fillMaxWidth()
            .height(120.dp)
            .background(usedColor)
            .padding(15.dp)
            .clickable(
                enabled = true,
                onClickLabel = null,
                role = null,
                onClick = {onLightBulbClicked(rid)}
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.philips_hue_light_bulb),
            contentDescription = "Light bulb image",
            contentScale = ContentScale.Fit,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    tint = textColor,
                    painter = painterResource(R.drawable.rounded_titlecase_30),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = lightBulbName,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    tint = textColor,
                    painter = painterResource(R.drawable.rounded_brightness_5_24),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "Brightness: $brightnessLevel%",
                    color = textColor
                )
            }
        }
        Icon(
            modifier = Modifier
                .size(70.dp),
            painter = painterResource(
                if (lightBulbOn) {
                    R.drawable.rounded_lightbulb_24
                } else {
                    R.drawable.rounded_light_off_24
                }
            ),
            contentDescription = null,
            tint = textColor
        )
    }
}

    @Preview(showBackground = true, widthDp = 412)
    @Composable
    private fun LightBulbItemPreview() {
        HuePiTheme(darkTheme = true) {
            LightBulbItem(
                color = Color.Yellow,
                lightBulbName = "Lorem ipasdum asdfguh sdfgjkuadg dfgkjhasdfg, gajghlks asgdjghdf gfdh dfgj dfvjfhk",
                brightnessLevel = 100.0F,
                lightBulbOn = true,
                onLightBulbClicked = {},
                rid = "loremimpsum2137",
            )
        }
    }