package com.klusio19.huepi.retrofit

import com.klusio19.huepi.model.ConnectionCheck
import com.klusio19.huepi.model.LightBulb
import retrofit2.Response
import retrofit2.http.GET

interface RaspberryPiAPIService {
    @GET("/check-connection")
    suspend fun checkConnection(): Response<ConnectionCheck>

    @GET("/get-all-lights-details")
    suspend fun getAllLightsDetails(): Response<List<LightBulb>?>

}