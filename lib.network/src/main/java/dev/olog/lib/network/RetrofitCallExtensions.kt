package dev.olog.lib.network

import kotlinx.coroutines.delay
import retrofit2.Response
import timber.log.Timber

@Deprecated(message = "replace with dev.olog.data.shared.retrofit.Result")
suspend fun <T> networkCall(repeatTimes: Int = 3, call: suspend () -> Response<T>): T? {
    var errorCode: Int? = null
    var errorMessage: String? = null

    repeat(repeatTimes) { iteration ->
        delay(1000L * iteration)
        val result = call()
        if (result.isSuccessful) {
            return result.body()
        }
        errorCode = result.code()
        errorMessage = result.message()
    }

    throw InvalidNetworkCall(errorCode, errorMessage)
}

@Deprecated(message = "replace with dev.olog.data.shared.retrofit.Result")
suspend fun <T> safeNetworkCall(
    repeatTimes: Int = 3,
    call: suspend () -> Response<T>
): T? {
    try {
        repeat(repeatTimes) { iteration ->
            delay(1000L * iteration)
            val result = call()
            if (result.isSuccessful) {
                return result.body()
            }
        }
    } catch (ex: Exception) {
        Timber.e(ex)
        ex.printStackTrace()
    }
    return null
}

internal class InvalidNetworkCall(statusCode: Int?, statusMessage: String?) :
    Throwable(message = "status code=$statusCode, message=$statusMessage")