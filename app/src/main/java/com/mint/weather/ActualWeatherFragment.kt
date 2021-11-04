package com.mint.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mint.weather.databinding.FragmentActualWeatherBinding
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ActualWeatherFragment : Fragment() {
    private var _binding: FragmentActualWeatherBinding? = null
    private val binding get() = _binding!!

    private val actualWeatherApi: WeatherApi by lazy { getActualWeatherApi("https://api.openweathermap.org/") }

    private var hourlyWeatherAdapter = HourlyWeatherAdapter(mutableListOf())
    private var dailyWeatherAdapter = DailyWeatherAdapter(mutableListOf())

    private var latitude: Double = 53.9
    private var longitude: Double = 27.5667

    private var fusedLocationProvider: FusedLocationProviderClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())

        checkLocationPermission()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActualWeatherBinding.inflate(inflater, container, false)
        binding.hourlyWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dailyWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    private fun checkLocationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    fusedLocationProvider?.lastLocation?.addOnSuccessListener(requireActivity()) { location ->
                        if (location != null) {
                            longitude = location.longitude
                            latitude = location.latitude
                            sendBaseRequests(latitude, longitude)
                        }
                    }
                } else {

//                    AlertDialog.Builder(requireContext())
//                        .setTitle("Данному приложению требуется разрешение опредения местоположения")
//                        .setMessage("При отсутствии разрешения некоторые функции могут не работать")
//                        .setPositiveButton(
//                            "Понятно"
//                        ) { _, _ ->
//                        }
//                        .create()
//                        .show()

                    sendBaseRequests(latitude, longitude)
                }
            }

        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                fusedLocationProvider?.lastLocation?.addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        longitude = location.longitude
                        latitude = location.latitude
                        sendBaseRequests(latitude, longitude)
                    }
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Данному приложению требуется разрешение на местоположения")
                    .setMessage("Показать диалог с запросом разрешения?")
                    .setPositiveButton(
                        "Да"
                    ) { _, _ ->
                        requestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                    .setNegativeButton("Нет, спасибо")
                    { _, _ ->
                        sendBaseRequests(latitude, longitude)
                    }
                    .create()
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun sendBaseRequests(lat: Double, lon: Double) {
        getWeatherNow(lat, lon)
        getCity(lat, lon)
    }

    private fun getCity(lat: Double, lon: Double) {
        val cities: Call<List<City>> = actualWeatherApi.getCity(lat, lon)

        cities.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {

                val city = response.body()?.firstOrNull()
                if (city != null) {
                    binding.cityName.text = city.localNames.ru ?: city.name
                }
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable?) {}
        })
    }

    private fun getWeatherNow(lat: Double, lon: Double) {
        val actualWeather: Call<ActualWeather> = actualWeatherApi.getActualWeather(lat, lon)

        actualWeather.enqueue(object : Callback<ActualWeather> {
            override fun onResponse(call: Call<ActualWeather>, response: Response<ActualWeather>) {

                val actualWeather = response.body()
                if (actualWeather != null) {
                    setWeather(
                        WeatherMain(
                            actualWeather.current.temp,
                            actualWeather.current.weather[0].description.replaceFirstChar { c -> c.uppercase() },
                            actualWeather.current.feelsLike,
                            actualWeather.current.weather[0].icon,
                            actualWeather.current.windSpeed,
                            actualWeather.current.windDeg,
                            actualWeather.current.pressure,
                            actualWeather.current.humidity
                        )
                    )
                    initHourlyRecycleView(actualWeather)
                    ititDailyRecyclerView(actualWeather)
                }
            }

            override fun onFailure(call: Call<ActualWeather>, t: Throwable) {
            }
        })
    }

    private fun initHourlyRecycleView(actualWeather: ActualWeather) {
        val now = System.currentTimeMillis() / 1000
        val hourlyWeather = actualWeather.hourly.map { HourWeather(it.dt, it.temp, it.weather[0].icon) }
        val sunrises = actualWeather.daily.map { daily -> Sunrise(daily.sunrise) }.filter { it.dateTime > now && it.dateTime < (now + 172800) }
        val sunsets = actualWeather.daily.map { daily -> Sunset(daily.sunset) }.filter { it.dateTime > now && it.dateTime < (now + 172800) }
        val list = sunrises + sunsets + hourlyWeather
        val sortedList = list.sortedBy { it.dateTime }
        hourlyWeatherAdapter = HourlyWeatherAdapter(sortedList)
        binding.hourlyWeatherRecyclerView.adapter = hourlyWeatherAdapter
    }

    private fun ititDailyRecyclerView(actualWeather: ActualWeather) {
        val dailyWeather = actualWeather.daily.map { DailyWeatherShort(it.dt, it.temp.day, it.temp.night, it.weather[0].icon, it.rain, it.windSpeed, WindDirections.getWindDirection(it.windDeg), it.windGust) }
        dailyWeatherAdapter = DailyWeatherAdapter(dailyWeather)
        binding.dailyWeatherRecyclerView.adapter = dailyWeatherAdapter

    }

    private fun setWeather(weather: WeatherMain) {
        binding.temp.text = "${weather.temp.toInt()}°C"
        binding.feelsLike.text = "Ощущается как: ${weather.feelsLike.toInt()}°C"
        binding.description.text = "${weather.description.replaceFirstChar { c -> c.uppercase() }}"

        val windDirection = WindDirections.getWindDirection(weather.windDeg)

        binding.windSpeed.text = "${weather.windSpeed.toInt()} м/с, ${windDirection.direction}"
        binding.windSpeed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.round_air_24, 0, windDirection.directionIcon, 0)
        binding.windSpeed.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        binding.pressure.text = "${(weather.pressure / 1.333).toInt()} мм рт.ст."
        binding.pressure.setCompoundDrawablesWithIntrinsicBounds(R.drawable.round_speed_24, 0, 0, 0)
        binding.pressure.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        binding.humidity.text = "${weather.humidity}%"
        binding.humidity.setCompoundDrawablesWithIntrinsicBounds(R.drawable.round_water_drop_24, 0, 0, 0)
        binding.humidity.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        Glide
            .with(binding.root)
            .load("https://openweathermap.org/img/wn/${weather.icon}@2x.png")
            .into(binding.icon)
    }

    private fun getActualWeatherApi(url: String): WeatherApi {
        val build = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request()

                val newUrl: HttpUrl = request.url()
                    .newBuilder()
                    .addQueryParameter("appid", "b7044fa387aaefecbb6a8888f3624867")
                    .build()

                val newRequest = request.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }
}

