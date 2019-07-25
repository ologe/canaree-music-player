package dev.olog.shared.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

@Suppress("DEPRECATION")
object NetworkUtils {

    @JvmStatic
    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = manager.activeNetworkInfo ?: return false
        val isWifi = activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
        val isMobile = activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
        return isWifi || isMobile
    }

    @JvmStatic
    fun isOnWiFi(context: Context): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

        return wifiManager?.isWifiEnabled ?: false &&
                wifiManager?.connectionInfo?.networkId != -1
    }

}