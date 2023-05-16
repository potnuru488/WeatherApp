package com.satish.weatherapp.repository

import com.satish.weatherapp.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getCurrentWeatherData(
        lat: String,
        lon: String,
        appId: String
    ) = apiHelper.getCurrentWeatherData(lat, lon, appId)

    suspend fun getCityWeatherData(
        q: String,
        appId: String
    ) = apiHelper.getCityWeatherData(q, appId)
}