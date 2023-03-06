package dev.olog.shared.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

@Suppress("DEPRECATION")
object NetworkUtils {

    private val allowedConnectionTypes = listOf(
        ConnectivityManager.TYPE_WIFI,
        ConnectivityManager.TYPE_WIMAX,
        ConnectivityManager.TYPE_ETHERNET
    )

    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun isOnWiFi(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return isConnected(context) && manager.activeNetworkInfo?.type in allowedConnectionTypes
    }

}