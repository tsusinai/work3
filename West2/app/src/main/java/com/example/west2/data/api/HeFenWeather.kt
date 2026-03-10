package com.example.west2.data.api

import com.example.west2.data.model.HeFenWeather
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiHeFenWeather{
    @GET("/v7/weather/3d")
    suspend fun getWeatherInf(
        @Query("key") key:String,
        @Query("location") location:String)
    : HeFenWeather


    @POST("/v7/weather/3d")
    suspend fun sendInf(
        @Field("key") key: String,
        @Field("location") location:String,
    ): HeFenWeather
}