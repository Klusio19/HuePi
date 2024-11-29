package com.klusio19.huepi.presentation.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.klusio19.huepi.model.LightBulb
import com.klusio19.huepi.retrofit.RetrofitInstance
import com.klusio19.huepi.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _lightBulbsList = MutableStateFlow<List<LightBulb>?>(null)
    val lightBulbsList: StateFlow<List<LightBulb>?> = _lightBulbsList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        fetchLightBulbs()
    }

    fun fetchLightBulbs() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val raspberryIpAddress = dataStoreManager.getRaspberryPiUrl()
                val raspberryApiKey = dataStoreManager.getRaspberryPiApiKey()

                val raspberryPiAPIService = RetrofitInstance.getClient(
                    baseUrl = raspberryIpAddress,
                    raspberryApiKey = raspberryApiKey
                )

                val lightBulbs = raspberryPiAPIService.getAllLightsDetails().body()
                _lightBulbsList.value = lightBulbs
            } catch (e: Exception) {
                _lightBulbsList.value = null
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

