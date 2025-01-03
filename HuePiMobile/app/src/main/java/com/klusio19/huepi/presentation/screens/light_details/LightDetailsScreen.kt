package com.klusio19.huepi.presentation.screens.light_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.klusio19.huepi.model.LightBulb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightDetailsScreen(
    lightBulb: LightBulb?,
    isFetchingData: Boolean,
    onRefreshLightBulbData: () -> Unit,
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit,
    onBrightnessSet: (Float) -> Unit,
    onColorChosen: (Float, Float, Float) -> Unit,
    onTempToColorTaskSet: (hueMin: Float, hueMax: Float, tempMin: Float, tempMax: Float) -> Unit,
    onStopTask: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        content = {
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isFetchingData,
                onRefresh = onRefreshLightBulbData,
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isFetchingData -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) { }
                    }
                    lightBulb == null -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("No light bulb details found!", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    else -> {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = lightBulb.name
                                        )
                                    },
                                    actions = {
                                        Icon(
                                            Icons.Rounded.Refresh,
                                            contentDescription = "RefreshButton",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .padding(end = 8.dp)
                                                .clickable(
                                                    enabled = true,
                                                    onClick = onRefreshLightBulbData
                                            )
                                        )
                                    }
                                )
                            },
                            content = { paddingValues ->
                                LightDetailsContent(
                                    lightBulb = lightBulb,
                                    modifier = Modifier.padding(paddingValues),
                                    onTurnOnSwitched = onTurnOn,
                                    onTurnOffSwitched = onTurnOff,
                                    onBrightnessSet = { level ->
                                        onBrightnessSet(level)
                                    },
                                    onColorChosen = { h, s, v ->
                                        onColorChosen(h, s, v)
                                    },
                                    onTempToColorTaskSet = { hueMin, hueMax, tempMin, tempMax ->
                                        onTempToColorTaskSet(hueMin, hueMax, tempMin, tempMax)
                                    },
                                    onStopTaskClicked = onStopTask,
                                    refreshLightBulbData = onRefreshLightBulbData
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}