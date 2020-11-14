package dev.olog.shared.android.utils

import android.content.Context
import android.net.ConnectivityManager
import dev.olog.shared.android.extensions.systemService

@Suppress("DEPRECATION")
object NetworkUtils {

    private val allowedConnectionTypes = listOf(
        ConnectivityManager.TYPE_WIFI,
        ConnectivityManager.TYPE_WIMAX,
        ConnectivityManager.TYPE_ETHERNET
    )

    fun isConnected(context: Context): Boolean {
        val manager = context.systemService<ConnectivityManager>()
        return manager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun isOnWiFi(context: Context): Boolean {
        val manager = context.systemService<ConnectivityManager>()
        return isConnected(context) && manager.activeNetworkInfo?.type in allowedConnectionTypes
    }

}