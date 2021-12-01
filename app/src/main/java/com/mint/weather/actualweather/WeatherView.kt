package com.mint.weather.actualweather

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategyByTag
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mint.weather.model.DailyWeatherShort
import com.mint.weather.model.Time
import com.mint.weather.model.WeatherMain

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface WeatherView : MvpView {

    fun setCityName(city: String)

    @StateStrategyType(value = AddToEndSingleStrategyByTag::class, tag = "SHOW_WEATHER")
    fun showWeather(weather: WeatherMain, hourlyWeather: List<Time>, dailyWeather: List<DailyWeatherShort>)
    @StateStrategyType(value = AddToEndSingleStrategyByTag::class, tag = "SHOW_WEATHER")
    fun showEmptyWeather()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun requireLocationPermission()

    @StateStrategyType(value = AddToEndSingleStrategyByTag::class, tag = "PROGRESS")
    fun showProgress()
    @StateStrategyType(value = AddToEndSingleStrategyByTag::class, tag = "PROGRESS")
    fun hideProgress()

}
