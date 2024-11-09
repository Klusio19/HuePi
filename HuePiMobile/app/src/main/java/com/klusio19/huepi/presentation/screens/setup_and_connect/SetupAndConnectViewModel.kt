package com.klusio19.huepi.presentation.screens.setup_and_connect

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.klusio19.huepi.retrofit.RaspberryPiAPIService
import com.klusio19.huepi.retrofit.RetrofitInstance
import com.klusio19.huepi.utils.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SetupAndConnectViewModel(application: Application): AndroidViewModel(application) {

    sealed class ConnectionState {
        object Idle : ConnectionState()
        object Loading : ConnectionState()
        data class Success(val message: String) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    private var _navigationEvent = Channel<Boolean>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var _connectionState = mutableStateOf<ConnectionState>(ConnectionState.Idle)
    private var _ipNumbersTextValue = mutableStateOf("")
    private var _apiKeyTextValue = mutableStateOf("")
    private var _isIpAddressValid = mutableStateOf(true)
    private var _isApiKeyValid = mutableStateOf(true)

    val ipNumbersTextValue: String get() = _ipNumbersTextValue.value
    val apiKeyTextValue: String get() = _apiKeyTextValue.value
    val isIpAddressValid: Boolean get() = _isIpAddressValid.value
    val isApiKeyValid: Boolean get() = _isApiKeyValid.value
    val connectionState: ConnectionState get() = _connectionState.value

    private val dataStoreManager = DataStoreManager(application)

    fun updateIpNumbersTextValue(newValue: String) {
        _ipNumbersTextValue.value = newValue
    }

    fun updateApiKeyTextValue(newValue: String) {
        _apiKeyTextValue.value = newValue
    }

    fun validateInputs() {
        _isIpAddressValid.value = _ipNumbersTextValue.value.length >= 13
        _isApiKeyValid.value = _apiKeyTextValue.value.isNotBlank()
    }

    fun raspiConnectionEstablished(){
        val ipNumbers = ipNumbersTextValue.substring(0,12)
        val portNumbers = ipNumbersTextValue.substring(12)
        val ipPart =
                "${ipNumbers.substring(0, 3)}." +
                "${ipNumbers.substring(3, 6)}." +
                "${ipNumbers.substring(6, 9)}." +
                ipNumbers.substring(9, 12)
        val formattedRaspberryUrl = "http://$ipPart:$portNumbers"
        val apiKey = apiKeyTextValue

        _connectionState.value = ConnectionState.Loading

        val raspberryPiAPIService: RaspberryPiAPIService = RetrofitInstance.getClient(formattedRaspberryUrl, apiKey)
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = raspberryPiAPIService.checkConnection()
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("RaspiConnection", response.body()!!.message)
                        if (response.body()!!.message == "OK") {
                            val saveUrlDeferred = async { dataStoreManager.saveRaspberryPiUrl(formattedRaspberryUrl) }
                            val saveApiKeyDeferred = async { dataStoreManager.saveRaspberryPiApiKey(apiKey) }
                            saveUrlDeferred.await()
                            saveApiKeyDeferred.await()
                            _connectionState.value = ConnectionState.Error("Successfully connected!")
                            _navigationEvent.send(true)
                        } else {
                            _connectionState.value = ConnectionState.Error("Connection failed: Invalid response")
                        }
                    } else {
                        _connectionState.value = ConnectionState.Error("Connection failed: ${response.message()}")
                    }
                }
            } catch (e: IOException) {
                Log.d("RaspiConnection", "IO EXCEPTION: ${e.message}")
                _connectionState.value = ConnectionState.Error("Connection failed: Network error")
            } catch (e: HttpException) {
                Log.d("RaspiConnection", "HTTP EXCEPTION: ${e.message}")
                _connectionState.value = ConnectionState.Error("Connection failed: ${e.message}")
            }
        }
    }
}

