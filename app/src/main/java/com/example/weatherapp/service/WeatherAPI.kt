package com.example.weatherapp.service

import com.example.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/

interface WeatherAPI {

    @GET("data/2.5/weather?&appid=c4a709eeec6a7a056bd43a60d0b9ae56&units=metric")

    fun getData(
        @Query("q") cityName: String
    ) : Single<WeatherModel>
}