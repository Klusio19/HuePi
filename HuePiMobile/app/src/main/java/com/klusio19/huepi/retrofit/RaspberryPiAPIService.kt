package com.klusio19.huepi.retrofit

import com.klusio19.huepi.model.ConnectionCheck
import retrofit2.Response
import retrofit2.http.GET

interface RaspberryPiAPIService {
    @GET("/check-connection")
    suspend fun checkConnection(): Response<ConnectionCheck>

}