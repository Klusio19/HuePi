package com.klusio19.huepi.presentation.screens.loading

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.klusio19.huepi.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoadingContent() {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                Image(
                    painter = painterResource(id = R.drawable.splash_screen_logo),
                    contentDescription = ""
                )
                CircularProgressIndicator()
            }
        )
    }
}


