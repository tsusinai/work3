package com.example.west2.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.west2.RetrofitInstance
import com.example.west2.data.model.HeFenWeather
import com.example.west2.data.model.HeFenWeatherCity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HeFenCityUiState {
    object Loading : HeFenCityUiState() // 加载中
    data class Success(val heFenWeatherCity: HeFenWeatherCity) : HeFenCityUiState() // 成功（携带用户数据）
    data class Error(val message: String) : HeFenCityUiState() // 失败（携带错误信息）
}



class HeFenWeatherCityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HeFenCityUiState>(HeFenCityUiState.Loading)
    val uiState: StateFlow<HeFenCityUiState> = _uiState.asStateFlow()

    init {
        fetchCityInf(key = "接口密钥", location = "合肥")
    }

    fun fetchCityInf(key:String,location:String) {
        _uiState.value = HeFenCityUiState.Loading

        viewModelScope.launch {
            try {
                val city = RetrofitInstance.apiHeFenWeatherCity.getCityInf(
                    key = key,
                    location = location
                )
                _uiState.value = HeFenCityUiState.Success(city)
            } catch (e: Exception) {
                _uiState.value = HeFenCityUiState.Error(e.message ?: "未知错误")
            }
        }
    }
}


sealed class HeFenWeatherUiState {
    object Loading : HeFenWeatherUiState() // 加载中
    data class Success(val heFenWeather: HeFenWeather) : HeFenWeatherUiState() // 成功（携带用户数据）
    data class Error(val message: String) : HeFenWeatherUiState() // 失败（携带错误信息）
}

class HeFenWeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HeFenWeatherUiState>(HeFenWeatherUiState.Loading)
    val uiState: StateFlow<HeFenWeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeatherInf(key = "接口密钥", location = "")
    }

    fun fetchWeatherInf(key:String,location:String) {
        _uiState.value = HeFenWeatherUiState.Loading

        viewModelScope.launch {
            try {
                val city = RetrofitInstance.apiHeFenWeather.getWeatherInf(
                    key = key,
                    location = location
                )
                _uiState.value = HeFenWeatherUiState.Success(city)
            } catch (e: Exception) {
                _uiState.value = HeFenWeatherUiState.Error(e.message ?: "未知错误")
            }
        }
    }


    val location: String by mutableStateOf("")
        private get

}