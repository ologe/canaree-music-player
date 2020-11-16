package dev.olog.lib.network.retrofit

import dev.olog.lib.network.ConnectivityManager
import dev.olog.lib.network.model.IoResult
import dev.olog.shared.exhaustive
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class IoResultCall<T>(
    dispatcher: CoroutineDispatcher,
    private val connectivityManager: ConnectivityManager,
    private val delegate: Call<T>
) : BaseCall<T, IoResult<T>>(dispatcher, delegate) {

    override suspend fun enqueueImpl(callback: Callback<IoResult<T>>) {
        val result = delegate.enqueue()
        when (result) {
            is InternalIoResult.Success -> onSuccess(result, callback)
            is InternalIoResult.ServerError -> onServerError(result, callback)
            is InternalIoResult.NetworkUnavailable -> retry(callback)
        }.exhaustive
    }

    private fun onSuccess(
        data: InternalIoResult.Success<T>,
        callback: Callback<IoResult<T>>
    ) {
        callback.onResponse(this, Response.success(data.toDomain()))
    }

    private fun onServerError(
        data: InternalIoResult.ServerError<T>,
        callback: Callback<IoResult<T>>
    ) {
        val response = IoResult.ServerError(data.code, data.message)
        callback.onResponse(this, Response.success(response))
    }

    private suspend fun retry(callback: Callback<IoResult<T>>) {
        connectivityManager.awaitNetworkAvailable()
        clone().enqueue(callback)
    }

    override fun cloneImpl(): Call<IoResult<T>> {
        return IoResultCall(dispatcher, connectivityManager, delegate.clone())
    }
}