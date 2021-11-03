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
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mint.weather.databinding.FragmentWeatherBinding
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val weatherApi: WeatherApi by lazy { getWeatherApi("https://api.openweathermap.org") }

    private var latitude: Double = 53.9
    private var longitude: Double = 27.5667

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())

        checkLocationPermission()
        fusedLocationProvider?.lastLocation?.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                longitude = location.longitude
                latitude = location.latitude
            }
            getWeatherNow(latitude, longitude)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            val city = binding.editCity.text.toString().trim()
            getCoordinates(city)

        }
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
                            getWeatherNow(latitude, longitude)
                            setValues(latitude, longitude)
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

                    getWeatherNow(latitude, longitude)
                    setValues(latitude, longitude)
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                fusedLocationProvider?.lastLocation?.addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        longitude = location.longitude
                        latitude = location.latitude
                        getWeatherNow(latitude, longitude)
                        setValues(latitude, longitude)
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
                        getWeatherNow(latitude, longitude)
                        setValues(latitude, longitude)
                    }
                    .create()
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun getWeatherNow(lat: Double, lon: Double) {
        val weatherNow: Call<WeatherNow> = weatherApi.getWeatherNow(lat, lon)

        weatherNow.enqueue(object : Callback<WeatherNow> {
            override fun onResponse(call: Call<WeatherNow>, response: Response<WeatherNow>) {

                val weatherNow = response.body()
                if (weatherNow != null) {
                    setWeather(weatherNow)
                }
            }

            override fun onFailure(call: Call<WeatherNow>, t: Throwable?) {}
        })
    }

    private fun setValues(lat: Double, lon: Double) {
        binding.lat.text = "Latitude: $lat"
        binding.lon.text = "Longitude: $lon"
    }

    private fun setWeather(weatherNow: WeatherNow) {
        binding.cityName.text = weatherNow.name
        binding.temp.text = "${weatherNow.main.temp.toInt()}°C"
        binding.feelsLike.text = "Ощущается как: ${weatherNow.main.feelsLike.toInt()}°C"
        binding.description.text = "${weatherNow.weather[0].description.replaceFirstChar { c -> c.uppercase() }}"

        Glide
            .with(binding.root)
            .load("https://openweathermap.org/img/wn/${weatherNow.weather[0].icon}@2x.png")
            .into(binding.icon)

    }

    private fun getCoordinates(city: String) {

        val cities: Call<List<City>> = weatherApi.getCities(city)

        cities.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {

                val city = response.body()?.firstOrNull()
                if (city != null) {
                    setValues(city.lat, city.lng)
                    getWeatherNow(city.lat, city.lng)
                }
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable?) {}
        })
    }

    private fun getWeatherApi(baseUrl: String): WeatherApi {
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
            .baseUrl(baseUrl)
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 1
    }

}


//    private fun getCoordinates(url: String) {
//        val request = StringRequest(
//            Request.Method.GET, url,
//            { response ->
//
//                val cities = Gson().fromJson<List<City>>(response, object : TypeToken<List<City>>() {}.type)
//
//                val city = cities.firstOrNull()
//                if (city != null) {
//                    setValues(city.lat, city.lon)
//                } else {
//                    Toast.makeText(context, "Не найдено", Toast.LENGTH_SHORT).show()
//                }
//            }
//        ) { error -> error.printStackTrace() }
//        queue.add(request)
//        val request = StringRequest(
//            Request.Method.GET,  //GET - API-запрос для получение данных
//            url, null, { response ->
//                try {
//                    val lat = response.getJSONObject(0).getDouble("lat")
//                    val lon = response.getJSONObject(0).getDouble("lon")
//                    setValues(lat, lon)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }) { error ->
//            error.printStackTrace()
//        }
//    }