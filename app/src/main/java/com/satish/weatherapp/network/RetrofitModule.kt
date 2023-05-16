package com.satish.weatherapp.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitModule {

    private val TIMEOUT = 60.toLong()

    companion object {
        fun getInstance(): RetrofitModule {
            return RetrofitModule()
        }
    }

    fun providesRetrofit(): Retrofit {
        val okHttpBuilder = providesOkHttpClient()
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder().baseUrl(NetworkConstants.BASE_URL)
            .client(okHttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun providesOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
    }
}