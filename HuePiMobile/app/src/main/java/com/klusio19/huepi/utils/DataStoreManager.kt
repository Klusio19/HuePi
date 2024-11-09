package com.klusio19.huepi.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = "connection_data")

class DataStoreManager(private val context: Context) {

    companion object {
        val RASPBERRY_PI_URL = stringPreferencesKey("RASPBERRY_URL")
        val RASPBERRY_PI_API_KEY = stringPreferencesKey("API_KEY")

        // Default values
        private const val DEFAULT_URL = "http://DATA-STORE.xyz"
        private const val DEFAULT_API_KEY = "DATA-STORE"
    }

    suspend fun saveRaspberryPiUrl(ipAddressToSave: String) {
        Log.d("DATASTORE", "\"saveRaspberryPiUrl()\" and argument retrieved: $ipAddressToSave")
        context.preferenceDataStore.edit { preferences ->
            preferences[RASPBERRY_PI_URL] = ipAddressToSave
        }
    }

    suspend fun saveRaspberryPiApiKey(apiKeyToSave: String) {
        Log.d("DATASTORE", "\"saveRaspberryPiApiKey()\" and argument retrieved: $apiKeyToSave")
        context.preferenceDataStore.edit { preferences ->
            preferences[RASPBERRY_PI_API_KEY] = apiKeyToSave
        }
    }

    suspend fun getRaspberryPiUrl(): String {
        Log.d("DATASTORE", "Getting URL from DataStore")
        return context.preferenceDataStore.data
            .map { preferences ->
                preferences[RASPBERRY_PI_URL] ?: DEFAULT_URL
            }.first()
    }

    suspend fun getRaspberryPiApiKey(): String {
        Log.d("DATASTORE", "Getting ApiKey from DataStore")
        return context.preferenceDataStore.data
            .map { preferences ->
                preferences[RASPBERRY_PI_API_KEY] ?: DEFAULT_API_KEY
            }.first()
    }

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }
}