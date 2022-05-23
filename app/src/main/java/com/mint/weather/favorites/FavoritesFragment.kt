package com.mint.weather.favorites

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mint.weather.App
import com.mint.weather.actualweather.ActualWeatherFragment.Companion.ARG_LOCATION
import com.mint.weather.databinding.FragmentFavoritesBinding
import com.mint.weather.model.CityWeatherLong
import com.mint.weather.model.CityWeatherShort
import javax.inject.Inject

class FavoritesFragment : Fragment() {

    @Inject
    lateinit var factory: FavoritesViewModelFactory.Factory

    private val viewModel: FavoritesViewModel by viewModels {
        factory.create(requireArguments().getParcelable<Location?>(ARG_LOCATION)!!) //todo nata если придет null, то не показывать текущее местоположение (в случае если нет пермишена)
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesCitiesAdapter = FavoriteCitiesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectFavoritesFragment(this)

        viewModel.cityName.observe(this) { name -> setCurrentCityName(name) }
        viewModel.showCurrentCityWeather.observe(this) { weather ->
            when (weather) {
                is FavoritesViewModel.StateShort.Empty -> showCurrentCityEmptyWeather()
                is FavoritesViewModel.StateShort.Data -> showCurrentCityWeather(weather.cityWeatherShort)
            }
        }
        viewModel.citiesWeatherList.observe(this) { list ->
            when (list) {
                is FavoritesViewModel.StateLong.Empty -> showEmptyFavoritesWeather()
                is FavoritesViewModel.StateLong.Data -> showFavoriteCitiesWeather(list.listCityWeatherLong)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = favoritesCitiesAdapter

        favoritesCitiesAdapter.cityListener = object : FavoriteCitiesAdapter.OnCityClickListener {
            override fun onItemClick(city: CityWeatherLong) {
                viewModel.cityClicked(city)
            }
        }
        binding.myToolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        return binding.root
    }

    private fun showCurrentCityWeather(weather: CityWeatherShort) {
        binding.currentLocation.setCityWeather(weather)
    }

    private fun setCurrentCityName(name: String) {
        binding.currentLocation.setCityName(name)
    }

    private fun showCurrentCityEmptyWeather() {
        binding.currentLocation.showEmptyWeather()
    }

    private fun showFavoriteCitiesWeather(list: List<CityWeatherLong>) {
        favoritesCitiesAdapter.submitList(list)
    }

    private fun showEmptyFavoritesWeather() {
        binding.favoriteCitiesError.visibility = View.VISIBLE //todo при перезапуске экрана сделать вьюшку невидимой
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}