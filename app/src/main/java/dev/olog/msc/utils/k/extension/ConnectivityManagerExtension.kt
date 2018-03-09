package dev.olog.msc.utils.k.extension

import android.net.ConnectivityManager

fun ConnectivityManager.isNetworkAvailable(): Boolean {
    val activeNetworkInfo = this.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun ConnectivityManager.isMobile(): Boolean{
    val activeNetworkInfo = this.activeNetworkInfo

    return activeNetworkInfo != null &&
            activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE ||
            activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE_DUN
}

fun ConnectivityManager.isWifi(): Boolean{
    val activeNetworkInfo = this.activeNetworkInfo
    return activeNetworkInfo != null &&
            activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI ||
            activeNetworkInfo.type == ConnectivityManager.TYPE_ETHERNET
}

fun ConnectivityManager.isSafeNetwork(canUseMobile: Boolean): Boolean{
    return isWifi() || (canUseMobile && isMobile())
}