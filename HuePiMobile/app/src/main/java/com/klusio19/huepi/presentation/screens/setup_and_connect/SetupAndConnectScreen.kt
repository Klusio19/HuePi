package com.klusio19.huepi.presentation.screens.setup_and_connect

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetupAndConnectScreen(
    onIpChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onValidateInputs: () -> Unit,
    onConnectClicked: () -> Unit,
    isIpAddressValid: Boolean,
    isApiKeyValid: Boolean,
    ipText: String,
    apiKeyText: String,
    connectionState: SetupAndConnectViewModel.ConnectionState
    ) {
    Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.background)){
        SetupAndConnectContent(
            onIpChange = onIpChange,
            onApiKeyChange = onApiKeyChange,
            onValidateInputs = onValidateInputs,
            onConnectClicked = onConnectClicked,
            isIpAddressValid = isIpAddressValid,
            isApiKeyValid = isApiKeyValid,
            ipText = ipText,
            apiKeyText = apiKeyText,
            connectionState = connectionState
        )
    }
}