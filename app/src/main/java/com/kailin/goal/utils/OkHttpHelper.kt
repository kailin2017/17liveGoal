package com.kailin.goal.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class OkHttpHelper private constructor() {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(OkHttpInterceptor.instance)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonHelper.instance.gson))
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    companion object {
        val instance: OkHttpHelper by lazy { OkHttpHelper() }
    }
}