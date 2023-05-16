package com.satish.weatherapp.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getCurrentWeatherData(
        lat: String,
        lon: String,
        appId: String
    ) = apiService.getCurrentWeatherData(lat, lon, appId)

    suspend fun getCityWeatherData(
        q: String,
        appId: String
    ) = apiService.getCityWeatherData(q, appId)
}