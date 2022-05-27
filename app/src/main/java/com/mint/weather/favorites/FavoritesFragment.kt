package com.mint.weather.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mint.weather.App
import com.mint.weather.actualweather.ActualWeatherFragment.Companion.ARG_WEATHER
import com.mint.weather.databinding.FragmentFavoritesBinding
import com.mint.weather.model.City
import com.mint.weather.model.CurrentCityWeather
import com.mint.weather.model.CurrentWeather
import com.mint.weather.model.FavoritesItem
import javax.inject.Inject

class FavoritesFragment : Fragment() {

    @Inject
    lateinit var factory: FavoritesViewModelFactory.Factory

    private val viewModel: FavoritesViewModel by viewModels {
         factory.create(requireArguments().getSerializable(ARG_WEATHER) as CurrentCityWeather?) //todo nata
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesCitiesAdapter = FavoriteCitiesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectFavoritesFragment(this)

        viewModel.showCitiesWeatherList.observe(this){
            showFavoriteCitiesWeather(it)
        }
        viewModel.showErrorFavoritesWeather.observe(this) {
            showEmptyFavoritesWeather()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = favoritesCitiesAdapter

//        favoritesCitiesAdapter.cityListener = object : FavoriteCitiesAdapter.OnCityClickListener {
//            override fun onItemClick(city: CityWeather) {
//                viewModel.cityClicked(city)
//            }
//        }
        binding.myToolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        return binding.root
    }

    private fun showFavoriteCitiesWeather(list: List<FavoritesItem>) {
        favoritesCitiesAdapter.submitList(list)
    }

    private fun showEmptyFavoritesWeather() {
        binding.favoriteCitiesTextError.visibility = View.VISIBLE //todo при перезапуске экрана сделать вьюшку невидимой
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}