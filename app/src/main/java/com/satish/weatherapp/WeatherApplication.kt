package com.satish.weatherapp

import android.app.Application
import com.satish.weatherapp.api.ApiHelper
import com.satish.weatherapp.api.ApiService
import com.satish.weatherapp.network.RetrofitModule

class WeatherApplication : Application() {

    companion object {
        fun getInstance(): WeatherApplication {
            return WeatherApplication()
        }

        private var mApiHelper: ApiHelper? = null

        fun getApiHelperInstance(): ApiHelper {
            if (mApiHelper == null) {
                val retrofitModule = RetrofitModule.getInstance()
                mApiHelper =
                    retrofitModule.providesRetrofit().create(ApiService::class.java)
                        .let { ApiHelper(it) }
            }
            return mApiHelper!!
        }
    }
}