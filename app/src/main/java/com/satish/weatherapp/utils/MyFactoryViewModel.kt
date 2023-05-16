package com.satish.weatherapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.satish.weatherapp.api.ApiHelper
import com.satish.weatherapp.repository.MainRepository
import com.satish.weatherapp.viewmodel.MainViewModel

class MyFactoryViewModel(private val apiHelper: ApiHelper) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}