package com.example.west2.data.api

import com.example.west2.data.model.HeFenWeatherCity
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiHeFenWeatherCity{
    @GET("/geo/v2/city/lookup")
    suspend fun getCityInf(
        @Query("key") key:String,
        @Query("location") location:String)
    : HeFenWeatherCity


    @POST("/geo/v2/city/lookup")
    suspend fun sendInf(
        @Field("key") key: String,
        @Field("location") location:String,
    ): HeFenWeatherCity
}