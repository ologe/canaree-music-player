package dev.olog.lib.network

import android.content.Context
import android.net.Network
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import android.net.ConnectivityManager as ConnectivityService

@Singleton
internal class ConnectivityManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val _flow = MutableStateFlow(0)
    val networkState: Flow<Boolean> = _flow.map { it > 0 }

    val isNetworkAvailable: Boolean
        get() = _flow.value > 0

    private val listener = object : ConnectivityService.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _flow.value = _flow.value + 1
        }

        override fun onLost(network: Network) {
            _flow.value = _flow.value - 1
        }
    }

    init {
        val service = context.applicationContext.getSystemService<ConnectivityService>()!!
        val request = NetworkRequest.Builder().build()
        service.registerNetworkCallback(request, listener)
    }

    suspend fun awaitNetworkAvailable() {
        _flow.filter { it > 0 }
            .take(1)
            .first()
    }

}