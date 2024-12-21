package com.klusio19.huepi.retrofit

import com.klusio19.huepi.model.ConnectionCheck
import com.klusio19.huepi.model.LightBulb
import com.klusio19.huepi.model.LightBulbStateChangeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RaspberryPiAPIService {
    @GET("/check-connection")
    suspend fun checkConnection(): Response<ConnectionCheck>

    @GET("/get-all-lights-details")
    suspend fun getAllLightsDetails(): Response<List<LightBulb>?>

    @GET("/get-details/{rid}")
    suspend fun getLightBulbDetails(
        @Path("rid") rid: String
    ): Response<LightBulb?>

    @GET("/turn-off/{rid}")
    suspend fun turnOffLightBulb(
        @Path("rid") rid: String
    ): Response<LightBulbStateChangeResponse?>

    @GET("/turn-on/{rid}")
    suspend fun turnOnLightBulb(
        @Path("rid") rid: String
    ): Response<LightBulbStateChangeResponse?>

    @GET("/change-brightness/{rid}")
    suspend fun setBrightness(
        @Path("rid") rid: String,
        @Query("level") level: Float
    ): Response<LightBulbStateChangeResponse?>

    @GET("/change-color/{rid}")
    suspend fun setColor(
        @Path("rid") rid: String,
        @Query("h") h: Float,
        @Query("s") s: Float,
        @Query("v") v: Float,
    ): Response<LightBulbStateChangeResponse?>
}