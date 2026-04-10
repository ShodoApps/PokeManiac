package com.shodo.android.api.core.factories

import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import java.util.concurrent.TimeUnit.MINUTES

object OkHttpFactory {

    fun okHttpClient(): OkHttpClient {
        return clientBuilder()
            .build()
    }

    private fun clientBuilder(): Builder {
        return Builder()
            .connectTimeout(2, MINUTES)
            .readTimeout(2, MINUTES)
            .writeTimeout(2, MINUTES)
            .addMarvelApiInterceptor()
    }

    private fun Builder.addMarvelApiInterceptor(): Builder =
        addInterceptor { chain ->
            val httpUrl = chain.request().url.newBuilder()
                .build()

            val requestBuilder = chain.request().newBuilder().url(httpUrl)

            chain.proceed(requestBuilder.build())
        }

}