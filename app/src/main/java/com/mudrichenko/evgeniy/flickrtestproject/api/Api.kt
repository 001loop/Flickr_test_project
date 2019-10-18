package com.mudrichenko.evgeniy.flickrtestproject.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Api {

    private val BASE_URL = "https://www.flickr.com/services/"
    private val CONNECT_TIMEOUT_MILLS: Long = 40000
    private val READ_TIMEOUT_MILLS: Long = 40000

    fun getEndpointInterface(retrofit: Retrofit): EndpointInterface = retrofit
            .create(EndpointInterface::class.java)

    fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //.addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(getHttpClient())
            .build()

    private fun getHttpClient(): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(CONNECT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(READ_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        return okHttpBuilder.build()
    }

    private fun getGson(): Gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .create()

}
