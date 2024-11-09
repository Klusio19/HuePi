package com.klusio19.huepi.presentation.screens.loading

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klusio19.huepi.navigation.Screen
import com.klusio19.huepi.retrofit.RetrofitInstance
import com.klusio19.huepi.utils.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

class LoadingViewModel(context: Application): ViewModel() {

    private val dataStoreManager = DataStoreManager(context)
    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute: StateFlow<String?> = _navigationRoute

    init {
        checkConnectionAndNavigate()
    }

    private fun checkConnectionAndNavigate() {
        viewModelScope.launch {
            try {
                val raspiUrl = withContext(Dispatchers.IO) {
                    dataStoreManager.getRaspberryPiUrl()
                }
                val raspiApiKey = withContext(Dispatchers.IO) {
                    dataStoreManager.getRaspberryPiApiKey()
                }

                val connectionEstablished = raspberryPiConnectionEstablished(raspiUrl, raspiApiKey)

                _navigationRoute.value = if (connectionEstablished) {
                    Screen.Home.route
                } else {
                    Screen.SetupAndConnect.route
                }
            } catch (e: Exception) {
                Log.e("LoadingViewModel", "Error during connection check", e)
                _navigationRoute.value = Screen.SetupAndConnect.route
            }
        }
    }

//    private suspend fun raspberryPiConnectionEstablished(url: String, apiKey: String) : Boolean {
//        Log.d("LOADING-VIEWMODEL", "url: $url")
//        Log.d("LOADING-VIEWMODEL", "apiKey: $apiKey")
//        var response: Response<ConnectionCheck>? = null
//        try {
//            val raspberryPiAPIService = RetrofitInstance.getClient(url, apiKey)
//            response = withContext(Dispatchers.IO) {
//                raspberryPiAPIService.checkConnection()
//            }
//            if (response.isSuccessful && response.body()?.message == "OK") {
//                return true
//            }
//        } catch (e: IOException) {
//            Log.d("RaspiConnection", "IO EXCEPTION: ${e.message}")
//            return false
//        } catch (e: HttpException) {
//            Log.d("RaspiConnection", "HTTP EXCEPTION: ${e.message}")
//            return false
//        }
//        return false
//    }
    private suspend fun raspberryPiConnectionEstablished(url: String, apiKey: String): Boolean {
        return try {
            val raspberryPiAPIService = RetrofitInstance.getClient(url, apiKey)
            val response = withContext(Dispatchers.IO) {
                raspberryPiAPIService.checkConnection()
            }
            response.isSuccessful && response.body()?.message == "OK"
        } catch (e: IOException) {
            Log.d("RaspiConnection", "IO EXCEPTION: ${e.message}")
            false
        } catch (e: HttpException) {
            Log.d("RaspiConnection", "HTTP EXCEPTION: ${e.message}")
            false
        }
    }
}