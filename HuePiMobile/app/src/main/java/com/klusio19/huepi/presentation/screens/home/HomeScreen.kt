package com.klusio19.huepi.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.klusio19.huepi.model.LightBulb

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    lightBulbsList: List<LightBulb>?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLightBulbClicked: (String) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        content = {
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isRefreshing -> {}

                    lightBulbsList.isNullOrEmpty() -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("No light bulbs found!", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    else -> {
                        HomeContent(
                            lightBulbsList = lightBulbsList,
                            onLightBulbClicked = onLightBulbClicked,
                            modifier = Modifier
                                .statusBarsPadding()
                                .navigationBarsPadding()
                        )
                    }
                }
            }
        }
    )
}




