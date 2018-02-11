package dev.olog.msc.utils.k.extension

import android.net.ConnectivityManager

fun ConnectivityManager.isNetworkAvailable(): Boolean {
    val activeNetworkInfo = this.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}