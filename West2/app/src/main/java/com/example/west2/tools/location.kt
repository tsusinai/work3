package com.example.west2.tools

import android.content.Context

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import android.location.Location
import android.os.LocaleList
import android.Manifest
import android.content.pm.PackageManager
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume

suspend fun getCurrentCityNameSimple(context: Context): String {
    return withContext(Dispatchers.IO) {
        // 1. 权限检查（无权限直接返回兜底）
        val hasFine = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine && !hasCoarse) {
            return@withContext "合肥"
        }

        // 2. 获取定位（失败返回兜底）
        val location = try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            suspendCancellableCoroutine<Location?> { cont ->
                client.lastLocation.addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            }
        } catch (e: Exception) {
            null
        }
        if (location == null || (location.latitude == 0.0 && location.longitude == 0.0)) {
            return@withContext "合肥"
        }

        // 3. 解析城市名（失败返回兜底）
        return@withContext try {
            val geocoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Geocoder(context, Locale.getDefault())
            } else {
                @Suppress("DEPRECATION")
                Geocoder(context, Locale.getDefault())
            }
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude, location.longitude, 1
            )
            // 优先取城市，无则取省份，再兜底
            addresses?.firstOrNull()?.let {
                it.locality ?: it.adminArea ?: "合肥"
            } ?: "合肥"
        } catch (e: IOException) {
            "合肥"
        } catch (e: Exception) {
            "合肥"
        }
    }
}
