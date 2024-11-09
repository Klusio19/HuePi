package com.klusio19.huepi.presentation.screens.setup_and_connect

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun SetupAndConnectContent(
    onIpChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onValidateInputs: () -> Unit,
    onConnectClicked: suspend () -> Unit,
    isIpAddressValid: Boolean,
    isApiKeyValid: Boolean,
    ipText: String,
    apiKeyText: String,
    connectionState: SetupAndConnectViewModel.ConnectionState
) {
    val focusManager = LocalFocusManager.current
    var connectClicked by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(connectionState) {
        when (connectionState) {
            is SetupAndConnectViewModel.ConnectionState.Success -> {
                Toast.makeText(context, connectionState.message, Toast.LENGTH_SHORT).show()
            }
            is SetupAndConnectViewModel.ConnectionState.Error -> {
                Toast.makeText(context, connectionState.message, Toast.LENGTH_SHORT).show()
            }
            else -> { /* do nothing */ }
        }
    }
    LaunchedEffect(isIpAddressValid, isApiKeyValid, connectClicked) {
        if (connectClicked && isIpAddressValid && isApiKeyValid) {
            scope.launch {
                onConnectClicked()
            }
//            onConnectClicked()
            connectClicked = false
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        IpAddressWithPortTextField(
            text = ipText,
            onTextChange = onIpChange,
            isError = !isIpAddressValid
        )
        ApiKeyTextField(
            text = apiKeyText,
            onTextChange = onApiKeyChange,
            isError = !isApiKeyValid,
            focusManager = focusManager,
            onDoneClick = { connectClicked = true },
            onValidateInputs = onValidateInputs
        )
        ConnectButton(
            onButtonClicked = {
                onValidateInputs()
                connectClicked = true
            },
            isLoadingButton = connectionState is SetupAndConnectViewModel.ConnectionState.Loading
        )
    }
}

@Composable
fun ConnectButton(
    onButtonClicked: () -> Unit,
    isLoadingButton: Boolean) {
    Button(
        shape = RoundedCornerShape(20.dp),
        onClick = onButtonClicked
    ) {
        Text( text = "Connect")
        if (isLoadingButton) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(26.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp
            )
        }
    }
}

@Composable
fun ApiKeyTextField(
    text: String,
    onTextChange: (String) -> Unit,
    isError: Boolean,
    focusManager: FocusManager,
    onDoneClick: () -> Unit,
    onValidateInputs: () -> Unit
) {
    TextField(
        value = text,
        onValueChange = { newTextValue ->
            onTextChange(newTextValue)
        },
        label = { Text(text = "Raspberry PI's server API key") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onValidateInputs()
            onDoneClick()
        }),
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    if (isError) {
        Text(
            text = "API key cannot be empty",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
    }
}


@Composable
fun IpAddressWithPortTextField(text: String, onTextChange: (String) -> Unit, isError: Boolean) {
    TextField(
        value = text,
        onValueChange = {newTextValue ->
            val digitsOnly = newTextValue.filter { it.isDigit() }
            val limitedText = digitsOnly.take(17)
            onTextChange(limitedText)
        },
        label = { Text("Raspberry Pi's IP address with port") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        visualTransformation = IpAddressWithPortVisualTransformation(),
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    if (isError) {
        Text(
            text = "IP address must be at least 13 characters long",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

class IpAddressWithPortVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText = StringBuilder()
        val ipPart = text.take(12)
        val portPart = text.drop(12).take(5)

        ipPart.forEachIndexed { i, character ->
            formattedText.append(character)
            if (i == 2 || i == 5 || i == 8) {
                formattedText.append(".")
            }
            if (i == 11) {
                formattedText.append(":")
            }
        }
        formattedText.append(portPart)

        val offsetMapping = IpAddressWithPortOffsetMapping(text.text)
        return TransformedText(AnnotatedString(formattedText.toString()), offsetMapping)
    }
}

class IpAddressWithPortOffsetMapping(private val digitsOnly: String) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        val maxTransformedLength = calculateMaxTransformedLength()
        var transformedOffset = offset

        if (offset > 2) transformedOffset += 1
        if (offset > 5) transformedOffset += 1
        if (offset > 8) transformedOffset += 1
        if (offset > 11) transformedOffset += 1

        return transformedOffset.coerceAtMost(maxTransformedLength)
    }

    override fun transformedToOriginal(offset: Int): Int {
        val maxOriginalLength = digitsOnly.length
        var originalOffset = offset

        if (offset > 3) originalOffset -= 1
        if (offset > 7) originalOffset -= 1
        if (offset > 11) originalOffset -= 1
        if (offset > 14) originalOffset -= 1

        return originalOffset.coerceAtMost(maxOriginalLength)
    }

    private fun calculateMaxTransformedLength(): Int {
        val ipLength = minOf(digitsOnly.length, 12)
        val portLength = digitsOnly.drop(12).take(5).length
        return ipLength + (ipLength / 3) + (if (ipLength == 12) 1 else 0) + portLength
    }
}
