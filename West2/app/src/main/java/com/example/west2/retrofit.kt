package com.example.west2

import com.example.west2.data.api.ApiHeFenWeather
import com.example.west2.data.api.ApiHeFenWeatherCity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance{
    private const val URL = "https://mx6hexyf38.re.qweatherapi.com"	//定义服务器地址，结尾需要/

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient() // 宽松模式，兼容不规范的 JSON
            .serializeNulls() // 序列化 null 值
            .create()
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // 使用自定义Gson配置
            .build()
    }


    val apiHeFenWeatherCity: ApiHeFenWeatherCity by lazy {
        retrofit.create(ApiHeFenWeatherCity::class.java)
    }

    val apiHeFenWeather: ApiHeFenWeather by lazy {
        retrofit.create(ApiHeFenWeather::class.java)
    }
}

