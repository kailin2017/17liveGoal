package com.kailin.goal.utils

import okhttp3.Interceptor
import okhttp3.Response

class OkHttpInterceptor private constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().run {
            build()
        }
        return chain.proceed(request)
    }

    companion object {
        val instance: OkHttpInterceptor by lazy { OkHttpInterceptor() }
    }
}