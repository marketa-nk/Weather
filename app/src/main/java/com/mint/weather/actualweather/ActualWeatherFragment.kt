package com.mint.weather.actualweather

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.AppBarLayout
import com.mint.weather.App
import com.mint.weather.R
import com.mint.weather.actualweather.adapter.DailyWeatherAdapter
import com.mint.weather.actualweather.adapter.HourlyWeatherAdapter
import com.mint.weather.databinding.FragmentActualWeatherBinding
import com.mint.weather.model.*
import com.mint.weather.toUiString
import javax.inject.Inject

class ActualWeatherFragment : Fragment() {

    @Inject
    lateinit var factory: ActualWeatherViewModel.ActualWeatherViewModelFactory

    private val viewModel: ActualWeatherViewModel by viewModels { factory }

    private var _binding: FragmentActualWeatherBinding? = null
    private val binding get() = _binding!!

    private val hourlyWeatherAdapter = HourlyWeatherAdapter()
    private val dailyWeatherAdapter = DailyWeatherAdapter()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val findCitiesResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    val name = place.name
                    val latLng = place.latLng
                    val cityId = place.id
                    if (latLng != null && cityId != null) {
                        viewModel.cityIsSelected(cityId, name, latLng)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                data?.let {
                    val status = Autocomplete.getStatusFromIntent(data)
                    Toast.makeText(requireContext(), getString(R.string.error_occurred, status.statusMessage), Toast.LENGTH_SHORT).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectActualWeatherFragment(this)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.permissionGranted(true)
            } else {
                viewModel.permissionGranted(false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentActualWeatherBinding.inflate(inflater, container, false)

        binding.hourlyWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dailyWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.hourlyWeatherRecyclerView.adapter = hourlyWeatherAdapter
        binding.dailyWeatherRecyclerView.adapter = dailyWeatherAdapter

        binding.appBarMain.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset == 0
        })
        binding.btnSearch.setOnClickListener {
            viewModel.searchPressed()
        }
        binding.btnGoToFavoritesFragment.setOnClickListener {
            viewModel.btnGoToFavoritesFragmentPressed()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.viewSwipedToRefresh()
        }
        binding.cityName.text = getString(R.string.app_name)
        binding.swipeRefresh.setColorSchemeResources(
            R.color.blue,
            R.color.light_blue,
            R.color.orange_600,
        )
        binding.btnFavoritesStar.setOnClickListener {
            viewModel.favoriteBtnPressed()
        }
        viewModel.checkLocationPermissionEvent.observe(this.viewLifecycleOwner) {
            viewModel.permissionGranted(checkLocationPermission())
        }
        viewModel.showProgress.observe(this.viewLifecycleOwner) { show ->
            binding.swipeRefresh.isRefreshing = show
        }
        viewModel.showWeather.observe(this.viewLifecycleOwner) {
            when (it) {
                is ActualWeatherViewModel.State.Empty -> showEmptyWeather()
                is ActualWeatherViewModel.State.Data -> showCurrentWeather(it.currentWeather, it.hourlyWeather, it.dailyWeather)
            }
        }
        viewModel.cityName.observe(this.viewLifecycleOwner) {
            setCityName(it)
        }
        viewModel.requireLocationPermissionEvent.observe(this.viewLifecycleOwner) {
            requireLocationPermission()
        }
        viewModel.showFindCitiesScreen.observe(this.viewLifecycleOwner) {
            launchFindCitiesActivity()
        }
        viewModel.fillTheFavoriteStar.observe(this.viewLifecycleOwner) { show ->
            showFavoriteStar(show)
        }
        viewModel.showFavoriteFragment.observe(this.viewLifecycleOwner){ location ->
            showFavoriteFragment(location)
        }

        return binding.root
    }

    private fun requireLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.permissionGranted(true)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionAlertDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showPermissionAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.needs_location_permission))
            .setMessage(getString(R.string.show_location_permission))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(getString(R.string.no_thanks)) { _, _ ->
                viewModel.permissionGranted(false)
            }
            .create()
            .show()
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun launchFindCitiesActivity() {
        val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ID)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.CITIES)
            .build(requireContext())
        findCitiesResultLauncher.launch(intent)
    }

    private fun showFavoriteFragment(location: Location?) {
        binding.root.findNavController().navigate(
            R.id.action_actualWeatherFragment_to_favoritesFragment,
            bundleOf(ARG_LOCATION to location)
        )
    }

    private fun setCityName(city: String) {
        binding.cityName.text = city
    }

    private fun showCurrentWeather(weather: WeatherMain, hourlyWeather: List<Time>, dailyWeather: List<DailyWeatherShort>) {

        binding.temp.text = getString(R.string.weather_temperature, weather.temp.toUiString())
        binding.feelsLike.text = getString(R.string.weather_feels_like, weather.feelsLike.toUiString())
        binding.description.text = weather.description.replaceFirstChar { c -> c.uppercase() }

        val windDirection = WindDirections.getWindDirection(weather.windDeg)

        binding.windSpeed.text = getString(R.string.weather_wind, weather.windSpeed.toUiString(), windDirection.direction)
        binding.windSpeed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_air_24, 0, windDirection.directionIcon, 0)
        binding.windSpeed.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        binding.pressure.text = getString(R.string.weather_pressure, (weather.pressureMM).toUiString())
        binding.pressure.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_speed_24, 0, 0, 0)
        binding.pressure.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        binding.humidity.text = getString(R.string.weather_humidity, weather.humidity.toUiString())
        binding.humidity.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_water_drop_16, 0, 0, 0)
        binding.humidity.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        Glide
            .with(binding.icon)
            .load(weather.iconUrl)
            .into(binding.icon)

        hourlyWeatherAdapter.submitList(hourlyWeather) {
            binding.hourlyWeatherRecyclerView.scrollToPosition(0)
        }

        dailyWeatherAdapter.submitList(dailyWeather)
    }

    private fun showEmptyWeather() {
        binding.temp.text = getString(R.string.weather_temperature, getString(R.string.empty_string))
        binding.feelsLike.text = getString(R.string.weather_feels_like, getString(R.string.empty_string))
        binding.description.text = getString(R.string.empty_string)

        binding.windSpeed.text = getString(R.string.weather_wind, getString(R.string.empty_string), getString(R.string.empty_string))
        binding.windSpeed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_air_24, 0, 0, 0)
        binding.windSpeed.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        binding.pressure.text = getString(R.string.weather_pressure, getString(R.string.empty_string))
        binding.pressure.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_speed_24, 0, 0, 0)
        binding.pressure.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        binding.humidity.text = getString(R.string.weather_humidity, getString(R.string.empty_string))
        binding.humidity.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_water_drop_16, 0, 0, 0)
        binding.humidity.compoundDrawablePadding = resources.getDimension(R.dimen.in_dp).toInt()

        Glide
            .with(binding.icon)
            .clear(binding.icon)

        hourlyWeatherAdapter.submitList(emptyList())
        dailyWeatherAdapter.submitList(emptyList())
    }

    private fun showFavoriteStar(show: Boolean?) {
        if (show == null) {
            binding.btnFavoritesStar.visibility = View.INVISIBLE
            binding.btnGoToFavoritesFragment.visibility = View.VISIBLE
        } else {
            binding.btnGoToFavoritesFragment.visibility = View.INVISIBLE
            binding.btnFavoritesStar.visibility = View.VISIBLE
            setStarImageSource(show)
        }
    }

    private fun setStarImageSource(show: Boolean) {
        if (show) {
            binding.btnFavoritesStar.setImageResource(R.drawable.ic_round_star_32)
        } else {
            binding.btnFavoritesStar.setImageResource(R.drawable.ic_round_star_border_32)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val ARG_LOCATION = "ARG_LOCATION"
    }
}

