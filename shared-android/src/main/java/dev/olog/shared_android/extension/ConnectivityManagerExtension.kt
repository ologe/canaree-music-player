package dev.olog.shared_android.extension

import android.net.ConnectivityManager

fun ConnectivityManager.isNetworkAvailable(): Boolean {
    val activeNetworkInfo = this.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}