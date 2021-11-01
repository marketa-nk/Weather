package com.mint.weather

import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
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

    private val queue: RequestQueue by lazy { Volley.newRequestQueue(requireContext()) }
    private val weatherApi: WeatherApi by lazy { getWeatherApi("https://api.openweathermap.org") }

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

//        val locationManager = ContextCompat.getSystemService<Any>(requireContext().LOCATION_SERVICE) as LocationManager?

        binding.btnSend.setOnClickListener {
            val city = binding.editCity.text.toString().trim()
            getCoordinates(city)

        }
    }

    private fun getWeatherNow(city: City) {
        val weatherNow: Call<WeatherNow> = weatherApi.getWeatherNow(city.lat, city.lng)

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
        binding.temp.text = "${weatherNow.main.temp} °C"
        binding.feelsLike.text = "${weatherNow.main.feelsLike} °C"
        binding.description.text = "${weatherNow.weather[0].description}"

        Glide
            .with(binding.root)
            .load("http://openweathermap.org/img/wn/${weatherNow.weather[0].icon}@2x.png")
            .into(binding.icon)

    }

    private fun getCoordinates(city: String) {

        val cities: Call<List<City>> = weatherApi.getCities(city)

        cities.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {

                val city = response.body()?.firstOrNull()
                if (city != null) {
                    setValues(city.lat, city.lng)
                    getWeatherNow(city)
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