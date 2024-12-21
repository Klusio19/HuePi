package com.klusio19.huepi.presentation.screens.light_details

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klusio19.huepi.model.LightBulb
import com.klusio19.huepi.retrofit.RaspberryPiAPIService
import com.klusio19.huepi.retrofit.RetrofitInstance
import com.klusio19.huepi.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LightDetailsViewModel(
    application: Application,
    val rid: String
) : ViewModel() {

    private val dataStoreManager = DataStoreManager(application)
    private val _lightBulb = MutableStateFlow<LightBulb?>(null)
    private lateinit var raspberryIpAddress: String
    private lateinit var raspberryApiKey: String
    private lateinit var raspberryPiAPIService: RaspberryPiAPIService
    val lightBulb: StateFlow<LightBulb?> = _lightBulb


    private val _isFetchingData = MutableStateFlow(true)
    val isFetchingData: StateFlow<Boolean> = _isFetchingData

    init {
        fetchLightBulb()
    }

    fun fetchLightBulb() {
        viewModelScope.launch {
            _isFetchingData.value = true
            try {
                raspberryIpAddress = dataStoreManager.getRaspberryPiUrl()
                raspberryApiKey = dataStoreManager.getRaspberryPiApiKey()

                raspberryPiAPIService = RetrofitInstance.getClient(
                    baseUrl = raspberryIpAddress,
                    raspberryApiKey = raspberryApiKey
                )

                val lightBulb = raspberryPiAPIService.getLightBulbDetails(rid).body()
                _lightBulb.value = lightBulb
            } catch (e: Exception) {
                Log.d("LightDetailsViewModel", "Error: $e")
                _lightBulb.value = null
            } finally {
                _isFetchingData.value = false
            }
        }
    }

    fun turnOnLightBulb() {
        viewModelScope.launch {
            val response = raspberryPiAPIService.turnOnLightBulb(rid)
        }
    }

    fun turnOffLightBulb() {
        viewModelScope.launch {
            val response = raspberryPiAPIService.turnOffLightBulb(rid)
        }
    }

    fun changeBrightness(level: Float) {
        viewModelScope.launch {
            val response = raspberryPiAPIService.setBrightness(rid, level)
        }
    }

    fun setColor(h: Float, s: Float, v: Float) {
        viewModelScope.launch {
            val response = raspberryPiAPIService.setColor(rid, h, s, v)
        }
    }
}

class LightDetailsViewModelFactory(
    private val application: Application,
    private val rid: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LightDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LightDetailsViewModel(application, rid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
