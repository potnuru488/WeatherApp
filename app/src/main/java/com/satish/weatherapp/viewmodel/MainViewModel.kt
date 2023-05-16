package com.satish.weatherapp.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satish.weatherapp.model.WeatherResponse
import com.satish.weatherapp.network.NetworkConstants
import com.satish.weatherapp.repository.MainRepository
import com.satish.weatherapp.utils.NetworkState
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val networkState = MutableLiveData(NetworkState.LOADED)
    private val _temperatureDetailsLiveData = MutableLiveData<WeatherResponse?>()
    val temperatureDetailsLiveData: LiveData<WeatherResponse?>
        get() = _temperatureDetailsLiveData

    private val _errorMessageLiveData = MutableLiveData<String?>()
    val errorMessageLiveData: LiveData<String?>
        get() = _errorMessageLiveData


    /*fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }*/

    fun getCityWeatherData(cityName : String) {
        viewModelScope.launch {
            networkState.postValue(NetworkState.LOADING)
            val responseResult = mainRepository.getCityWeatherData(cityName, NetworkConstants.API_KEY)
            networkState.postValue(NetworkState.LOADED)
            if (responseResult.isSuccessful) {
                _temperatureDetailsLiveData.postValue(responseResult.body())
            } else {
                _errorMessageLiveData.postValue(
                    responseResult.message()
                )
            }
        }
    }

    fun fetchCurrentLocationWeather(latitude: String, longitude: String) {
        viewModelScope.launch {
            networkState.postValue(NetworkState.LOADING)
            val responseResult = mainRepository.getCurrentWeatherData(latitude, longitude, NetworkConstants.API_KEY)
            networkState.postValue(NetworkState.LOADED)
            if (responseResult.isSuccessful) {
                _temperatureDetailsLiveData.postValue(responseResult.body())
            } else {
                _errorMessageLiveData.postValue(
                    responseResult.message()
                )
            }
        }
    }
}