package dev.olog.msc.utils.k.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dev.olog.msc.presentation.image.creation.ImagesCreator
import io.reactivex.Flowable
import io.reactivex.rxkotlin.withLatestFrom

fun Connectivity.isConnected(): Boolean {
    return this.state == NetworkInfo.State.CONNECTED
}

inline fun <T, R> Flowable<T>.ifNetworkIsAvailable(context: Context, crossinline combiner: (T, Boolean) -> R): Flowable<R> {
    return this.withLatestFrom(observeSafeNetwork(context), combiner)
}

fun observeSafeNetwork(context: Context): Flowable<Boolean> {
    return ReactiveNetwork.observeNetworkConnectivity(context)
            .map {
                val isConnected = ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED).test(it)
                val isWifi = ConnectivityPredicate.hasType(ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET).test(it)
                val isMobile = ConnectivityPredicate.hasType(ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_DUN).test(it)
                isConnected && (isWifi || (ImagesCreator.CAN_DOWNLOAD_ON_MOBILE && isMobile))
            }
            .asFlowable()
}