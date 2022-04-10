package dev.olog.data.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import retrofit2.Response

internal suspend fun <T> networkCall(repeatTimes: Int = 3, call: suspend () -> Response<T>): T? {
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
        yield()
    }

    throw InvalidNetworkCall(errorCode, errorMessage)
}

internal suspend fun <T> safeNetworkCall(
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
            yield()
        }
    } catch (ex: Throwable) {
//        ex.printStackTrace()
    }
    return null
}

internal class InvalidNetworkCall(statusCode: Int?, statusMessage: String?) :
    Throwable(message = "status code=$statusCode, message=$statusMessage")