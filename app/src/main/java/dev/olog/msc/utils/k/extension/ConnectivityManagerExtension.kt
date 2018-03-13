package dev.olog.msc.utils.k.extension

import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity

fun Connectivity.isConnected(): Boolean {
    return this.state == NetworkInfo.State.CONNECTED
}