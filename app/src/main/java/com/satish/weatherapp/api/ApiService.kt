package com.satish.weatherapp.api

import com.satish.weatherapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("APPID") appid: String
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getCityWeatherData(
        @Query("q") q: String,
        @Query("APPID") appid: String
    ): Response<WeatherResponse>
}