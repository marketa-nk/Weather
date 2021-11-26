package com.mint.weather.actualweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.mint.weather.R
import com.mint.weather.actualweather.adapter.DailyWeatherAdapter
import com.mint.weather.actualweather.adapter.HourlyWeatherAdapter
import com.mint.weather.databinding.FragmentActualWeatherNewBinding
import com.mint.weather.model.DailyWeatherShort
import com.mint.weather.model.Time
import com.mint.weather.model.WeatherMain
import com.mint.weather.model.WindDirections

class ActualWeatherFragment : MvpAppCompatFragment(), WeatherView {

    @InjectPresenter
    lateinit var actualWeatherPresenter: ActualWeatherPresenter

    private var _binding: FragmentActualWeatherNewBinding? = null
    private val binding get() = _binding!!

    private val hourlyWeatherAdapter = HourlyWeatherAdapter()
    private val dailyWeatherAdapter = DailyWeatherAdapter()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                actualWeatherPresenter.permissionGranted(true)
            } else {
                actualWeatherPresenter.permissionGranted(false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActualWeatherNewBinding.inflate(inflater, container, false)

        binding.hourlyWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dailyWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.hourlyWeatherRecyclerView.adapter = hourlyWeatherAdapter
        binding.dailyWeatherRecyclerView.adapter = dailyWeatherAdapter

        binding.appBarMain.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset == 0
        })

        binding.swipeRefresh.setOnRefreshListener {
            // Update the text view text with a random number
            actualWeatherPresenter.swipeToRefresh()

            // Hide swipe to refresh icon animation
            binding.swipeRefresh.isRefreshing = false//todo

        }

        binding.swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        return binding.root
    }

    override fun requireLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                actualWeatherPresenter.permissionGranted(true)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Данному приложению требуется разрешение на местоположения")
                    .setMessage("Показать диалог с запросом разрешения?")
                    .setPositiveButton("Да") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Нет, спасибо") { _, _ ->
                        actualWeatherPresenter.permissionGranted(false)
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


    override fun setCityName(city: String) {
        binding.cityName.text = city
    }

    override fun showWeather(weather: WeatherMain, hourlyWeather: List<Time>, dailyWeather: List<DailyWeatherShort>) {

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

        hourlyWeatherAdapter.submitList(hourlyWeather)
        dailyWeatherAdapter.submitList(dailyWeather)
    }
}

