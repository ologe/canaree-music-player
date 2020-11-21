package dev.olog.lib.network.retrofit

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun <T> Call<T>.enqueue(): InternalIoResult<T> {
    val call = this

    return suspendCancellableCoroutine {
        if (call.isCanceled) {
            it.resumeWithException(CancellationException())
            return@suspendCancellableCoroutine
        }

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    it.resume(InternalIoResult.Success(response.body()!!))
                } else {
                    it.resume(InternalIoResult.ServerError(response.code(), response.message() ?: ""))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                t.printStackTrace()
                if (t is IOException) {
                    it.resume(InternalIoResult.NetworkUnavailable())
                } else {
                    it.resume(InternalIoResult.ServerError(500, ""))
                }
            }
        })

        it.invokeOnCancellation {
            call.cancel()
        }
    }
}
