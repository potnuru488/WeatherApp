package com.satish.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.satish.weatherapp.databinding.ActivityMainBinding
import com.satish.weatherapp.model.WeatherResponse
import com.satish.weatherapp.utils.MyFactoryViewModel
import com.satish.weatherapp.utils.NetworkState
import com.satish.weatherapp.viewmodel.MainViewModel
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 101
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel = ViewModelProvider(
            this,
            MyFactoryViewModel(
                WeatherApplication.getApiHelperInstance()
            )
        )[MainViewModel::class.java]

        binding.mainViewModel = mainViewModel
        setContentView(binding.root)

        mainViewModel.networkState.observe(this) {
            if (it == NetworkState.LOADING) binding.progressBar.visibility =
                View.VISIBLE else binding.progressBar.visibility = View.GONE
        }
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        binding.citySearch.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                getCityWeather(binding.citySearch.text.toString())
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    binding.citySearch.clearFocus()
                }
                return@setOnEditorActionListener true
            }
            else {
                return@setOnEditorActionListener false
            }
        }


        binding.currentLocation.setOnClickListener {
            getCurrentLocation()
        }

        mainViewModel.errorMessageLiveData.observe(this) {
            binding.rootLayout.visibility = View.GONE
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        mainViewModel.temperatureDetailsLiveData.observe(this) {
            it?.run {
                binding.rootLayout.visibility = View.VISIBLE
                setData(this)
            }
        }
    }

    private fun getCityWeather(cityName: String) {
        mainViewModel.getCityWeatherData(cityName)
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {
        mainViewModel.fetchCurrentLocationWeather(latitude, longitude)
    }

    private fun getCurrentLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProvider.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            currentLocation = location
                            binding.progressBar.visibility = View.VISIBLE

                            fetchCurrentLocationWeather(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                        }
                    }

            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE)
                as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }


    private fun setData(body: WeatherResponse) {
        binding.apply {
            val currentDate = SimpleDateFormat("dd/MM/yyyy hh:mm").format(Date())
            dateTime.text = currentDate.toString()
            maxTemp.text = "Max " + k2c(body?.main?.temp_max!!) + "°"
            minTemp.text = "Min " + k2c(body?.main?.temp_min!!) + "°"
            temp.text = "" + k2c(body?.main?.temp!!) + "°"
            weatherTitle.text = body.weather[0].main
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sunriseValue.text = ts2td(body.sys.sunrise.toLong())
                sunsetValue.text = ts2td(body.sys.sunset.toLong())
            } else {
                sunriseValue.text = changeTime(body.sys.sunrise.toLong())
                sunsetValue.text = changeTime(body.sys.sunset.toLong())
            }
            pressureValue.text = body.main.pressure.toString()
            humidityValue.text = body.main.humidity.toString() + "%"
            tempFValue.text = "" + (k2c(body.main.temp).times(1.8)).plus(32)
                .roundToInt() + "°"

            citySearch.setText(body.name)

            feelsLike.text = "" + k2c(body.main.feels_like) + "°"

            windValue.text = body.wind.speed.toString() + "m/s"

            groundValue.text = body.main.grnd_level.toString()

            seaValue.text = body.main.sea_level.toString()

            countryValue.text = body.sys.country

        }
        updateUI(body.weather[0].id)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ts2td(ts: Long): String {
        val localTime = ts.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()

        }
        return localTime.toString()
    }

    fun changeTime(timeInMilliseconds: Long?): String? {
        val date = Date((timeInMilliseconds ?: 0) * 1000L)
        val sdf: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(date)
    }

    private fun k2c(t: Double): Double {

        var intTemp = t

        intTemp = intTemp.minus(273)

        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }


    private fun updateUI(id: Int) {

        binding.apply {

            when (id) {

                //Thunderstorm
                in 200..232 -> {

                    weatherImg.setImageResource(R.drawable.ic_storm_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)

                }

                //Drizzle
                in 300..321 -> {

                    weatherImg.setImageResource(R.drawable.ic_few_clouds)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.drizzle_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.drizzle_bg)

                }

                //Rain
                in 500..531 -> {

                    weatherImg.setImageResource(R.drawable.ic_rainy_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.rain_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.rain_bg)

                }

                //Snow
                in 600..622 -> {

                    weatherImg.setImageResource(R.drawable.ic_snow_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.snow_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.snow_bg)

                }

                //Atmosphere
                in 701..781 -> {

                    weatherImg.setImageResource(R.drawable.ic_broken_clouds)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.atmosphere_bg)


                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.atmosphere_bg)

                }

                //Clear
                800 -> {

                    weatherImg.setImageResource(R.drawable.ic_clear_day)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clear_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clear_bg)

                }

                //Clouds
                in 801..804 -> {

                    weatherImg.setImageResource(R.drawable.ic_cloudy_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clouds_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clouds_bg)

                }
                //unknown
                else -> {

                    weatherImg.setImageResource(R.drawable.ic_unknown)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.unknown_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.unknown_bg)

                }
            }

        }

    }
}