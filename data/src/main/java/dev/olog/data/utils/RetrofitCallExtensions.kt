package dev.olog.data.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import retrofit2.Response

suspend fun<T> Deferred<Response<T>>.awaitRepeat(repeatTimes: Int = 3): T? {
    var errorCode : Int? = null
    var errorMessage : String? = null

    repeat(repeatTimes) { iteration ->
        delay(500L * iteration)
        val result = this.await()
        if (result.isSuccessful) {
            return result.body()
        }
        errorCode = result.code()
        errorMessage = result.message()
        yield()
    }

    throw InvalidNetworkCall(errorCode, errorMessage)
}

class InvalidNetworkCall(statusCode: Int?, statusMessage: String?)
    : Throwable(message = "status code=$statusCode, message=$statusMessage")