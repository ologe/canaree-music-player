package dev.olog.data.shared.retrofit.call

import dev.olog.data.shared.retrofit.IoResult
import dev.olog.shared.exhaustive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.pow

const val RETRY_DELAY = 1000
const val MAX_RETRY_COUNT = 3
const val HEADER_RETRY_AFTER = "Retry-After"
const val DEFAULT_RETRY_AFTER = 60L // seconds

class IoResultCall<T : Any>(
    dispatcher: CoroutineDispatcher,
    retry: Int,
    proxy: Call<T>,
    private val exceptions: MutableList<Throwable>
) : AbsCall<T, IoResult<T>>(dispatcher, retry, proxy) {

    override suspend fun enqueueImpl(callback: Callback<IoResult<T>>) {
        if (retry >= MAX_RETRY_COUNT) {
            val exceptions = this.exceptions.mapNotNull { it.message }.distinct()
            Timber.d("max retry reached, $exceptions")
            callback.onResponse(
                this,
                Response.success(IoResult.Error.Generic(Exception(exceptions.joinToString())))
            )
            return
        }

        val callResult = suspendCancellableCoroutine<IoResult<T>> {
            delegate.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    it.resume(buildResponse(response))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    exceptions.add(t)
                    it.resume(IoResult.Error.Generic(t))
                }
            })
        }

        when (callResult) {
            is IoResult.Success<T>,
            is IoResult.Error.NetworkError -> callback.onResponse(this, Response.success(callResult))
            is IoResult.Error.TooManyRequests -> retryAfter(callResult.retryAfter, callback)
            is IoResult.Error.Generic -> {
                exceptions.add(callResult.exception)
                val expDelay = (RETRY_DELAY * 2.0.pow(max(0, retry - 1))).toLong()
                retryAfter(expDelay, callback)
            }
        }.exhaustive
    }

    private fun buildResponse(response: Response<T>): IoResult<T> {
        return when (response.code()) {
            200 -> IoResult.Success(response.body()!!)
            429 -> {
                val retryAfter = response.headers()[HEADER_RETRY_AFTER]
                    ?.toLongOrNull()
                    ?: DEFAULT_RETRY_AFTER
                IoResult.Error.TooManyRequests(retryAfter)
            }
            else -> IoResult.Error.Generic(Exception("code=${response.code()}, error=${response.errorBody()?.string()}"))
        }
    }

    private suspend fun retryAfter(millis: Long, callback: Callback<IoResult<T>>) {
        delay(millis)
        clone().enqueue(callback)
    }

    override fun cloneImpl(retry: Int): Call<IoResult<T>> {
        return IoResultCall(dispatcher, retry, delegate.clone(), exceptions)
    }
}