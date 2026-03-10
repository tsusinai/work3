package com.example.west2.tools

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.west2.data.model.Weather
import com.example.west2.viewmodel.HeFenCityUiState
import com.example.west2.viewmodel.HeFenWeatherCityViewModel
import com.example.west2.viewmodel.HeFenWeatherUiState
import com.example.west2.viewmodel.HeFenWeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.jar.Manifest


@Composable
fun WeatherLoader(
    city: String?,
    onWeatherLoaded: (Weather) -> Unit,
    onError: (String) -> Unit
) {
    // 获取 ViewModel 实例（全局单例）
    val heFenWeatherCityViewModel: HeFenWeatherCityViewModel = viewModel()
    val heFenWeatherViewModel: HeFenWeatherViewModel = viewModel()

    // 收集城市/天气状态（跟随生命周期）
    val uiCityState by heFenWeatherCityViewModel.uiState.collectAsStateWithLifecycle()
    val uiWeatherState by heFenWeatherViewModel.uiState.collectAsStateWithLifecycle()

    // 记住城市 ID（避免重组丢失）
    var cityId by remember { mutableStateOf("") }

    LaunchedEffect(city) {
        // 城市为空时，直接返回错误
        if (city.isNullOrBlank()) {
            onError("城市名不能为空")
            return@LaunchedEffect
        }
        // 发起城市查询请求
        heFenWeatherCityViewModel.fetchCityInf(
            key = "3bb259f875ca4ef3bc215161252d42f2",
            location = city
        )
    }

    // 处理城市查询状态
    when (val state = uiCityState) {
        is HeFenCityUiState.Loading -> { /* 加载中，可在UI层展示加载动画 */ }
        is HeFenCityUiState.Success -> {
            // 安全获取第一个城市 ID（避免空指针）
            val firstCity = state.heFenWeatherCity.location.firstOrNull()
            if (firstCity != null) {
                cityId = firstCity.id
                // 城市 ID 变化时，请求对应天气
                LaunchedEffect(cityId) {
                    heFenWeatherViewModel.fetchWeatherInf(
                        key = "3bb259f875ca4ef3bc215161252d42f2",
                        location = cityId
                    )
                }
            } else {
                onError("未查询到该城市数据")
            }
        }
        is HeFenCityUiState.Error -> {
            onError("城市查询失败：${state.message}")
        }
    }

    // 处理天气查询状态
    when (val state = uiWeatherState) {
        is HeFenWeatherUiState.Loading -> { /* 加载中 */ }
        is HeFenWeatherUiState.Success -> {
            // 安全获取第一条天气数据
            val dailyWeather = state.heFenWeather.daily.firstOrNull()
            if (dailyWeather != null) {
                // 异步回调返回有效天气数据
                onWeatherLoaded(dailyWeather)
            } else {
                onError("未查询到该城市天气数据")
            }
        }
        is HeFenWeatherUiState.Error -> {
            onError("天气查询失败：${state.message}")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun WeatherDisplay() {
    var city by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
            val cityName = getCurrentCityNameSimple(context)
        city = cityName
        println("获取当前城市：$city")
    }

    // 记住天气数据
    var weatherData by remember { mutableStateOf<Weather?>(null) }
    var errorMsg by remember { mutableStateOf("") }

    // 天气加载器
    WeatherLoader(
        city = city,
        onWeatherLoaded = { weather ->
            weatherData = weather
            errorMsg = ""
        },
        onError = { msg ->
            errorMsg = msg
            weatherData = null
        }
    )

    Text(text = "天气信息：", fontSize = 20.sp, fontFamily = FontFamily.Monospace , fontWeight = Bold,modifier = Modifier.fillMaxWidth())
    // UI 渲染：根据状态展示内容
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
            ){
                when {
                    errorMsg.isNotBlank() -> {
                        // 展示错误信息
                        Text(text = errorMsg, color = Color.Red)
                    }
                    weatherData != null -> {
                        // 展示天气数据
                        val weather = weatherData!!

                Row(modifier=Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                ){
                    Column{
                        Text(
                            text = "当前天气： 日间：${weather.textDay} 晚间：${weather.textNight}",
                            fontSize = 15.sp,
                        )
                        Text(
                            text="当前气温：${weather.tempMin}-${weather.tempMax} ℃  , 城市：${city}",
                            fontSize = 14.sp
                        )
                    }
                }
            }
                    else -> {
                        // 加载中
                        Text(text = "正在查询天气...")
                    }
        }
    }
}

//权限检查
fun checkLocationPermission(context: Context): Boolean {
    val fineGranted = context.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = context.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    return fineGranted || coarseGranted
}
