package com.klusio19.huepi.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
//    fun setUrl(url: String) {
//        BASE_URL = url
//    }
//    fun setApiKey(key: String) {
//        API_KEY = key
//    }
//    private var BASE_URL: String = "http://placeholder.123.xyz"
//    private var API_KEY: String = "abc"
//    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .addInterceptor(
//            Interceptor { chain ->
//                val request: Request = chain.request()
//                    .newBuilder()
//                    .header("api-key", API_KEY)
//                    .build()
//                chain.proceed(request)
//            }
//        )
//        .build()
//    private val moshi = Moshi.Builder()
//        .add(KotlinJsonAdapterFactory())
//        .build()
//
//    val raspberryPiAPIService: RaspberryPiAPIService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .client(okHttpClient)
//            .build()
//            .create(RaspberryPiAPIService::class.java)
//    }
//    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String, raspberryApiKey: String): RaspberryPiAPIService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(
                Interceptor { chain ->
                    val request: Request = chain.request()
                        .newBuilder()
                        .header("api-key", raspberryApiKey)
                        .build()
                    chain.proceed(request)
                }
            ).build()

        val raspberryPiAPIService: RaspberryPiAPIService by lazy {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
                .create(RaspberryPiAPIService::class.java)
        }

        return raspberryPiAPIService
    }
}