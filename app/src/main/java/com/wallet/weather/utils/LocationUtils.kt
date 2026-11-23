package com.wallet.weather.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.wallet.weather.bean.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.roundToInt

/**
 * 定位
 */
internal object LocationUtils {
    suspend fun currentCity(context: Context): City? {
        val manager = context.getSystemService(LocationManager::class.java) ?: return null
        val enabledProviders = manager.getProviders(true)
        val provider = listOf(LocationManager.NETWORK_PROVIDER, "fused")
            .firstOrNull { it in enabledProviders } ?: return null
        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val location = try {
            withTimeoutOrNull(8_000) { requestLocation(context, manager, provider) }
        } catch (_: SecurityException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
        return location?.let { resolve(context, it) }
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    private suspend fun requestLocation(
        context: Context,
        manager: LocationManager,
        provider: String
    ): Location? = suspendCancellableCoroutine { continuation ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val signal = CancellationSignal()
            continuation.invokeOnCancellation { signal.cancel() }
            manager.getCurrentLocation(provider, signal, context.mainExecutor) { location ->
                continuation.resume(location)
            }
        } else {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) = complete(location)
                override fun onProviderDisabled(provider: String) = complete(null)
                override fun onProviderEnabled(provider: String) = Unit
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

                private fun complete(location: Location?) {
                    manager.removeUpdates(this)
                    if (continuation.isActive) continuation.resume(location)
                }
            }
            manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            continuation.invokeOnCancellation { manager.removeUpdates(listener) }
        }
    }

    private suspend fun resolve(context: Context, location: Location): City {
        val name = withContext(Dispatchers.IO) {
            runCatching {
                Geocoder(context, Locale.SIMPLIFIED_CHINESE)
                    .getFromLocation(location.latitude, location.longitude, 1)
                    ?.firstOrNull()?.let { it.locality ?: it.subAdminArea ?: it.adminArea }
            }.getOrNull()
        }
        val lat = (location.latitude * 100).roundToInt() / 100.0
        val lon = (location.longitude * 100).roundToInt() / 100.0
        return City("location:$lat:$lon", name ?: "当前位置", lat, lon)
    }
}
