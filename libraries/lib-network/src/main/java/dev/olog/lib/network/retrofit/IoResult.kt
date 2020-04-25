package dev.olog.lib.network.retrofit

import java.io.PrintWriter
import java.io.StringWriter

sealed class IoResult<out T : Any> {

    data class Success<T : Any>(val data: T) : IoResult<T>()
    sealed class Error : IoResult<Nothing>() {
        object NetworkError : Error()
        data class TooManyRequests(val retryAfter: Long) : Error()
        data class Generic(val exception: Throwable) : Error()
    }

}

suspend inline fun <T : Any, R : Any> IoResult<T>.map(crossinline mapper: suspend (T) -> R): IoResult<R> {
    return when (this) {
        is IoResult.Success<T> -> IoResult.Success(mapper(data))
        is IoResult.Error -> this
    }
}

suspend inline fun <T : Any, R : Any> IoResult<T>.flatMap(crossinline mapper: suspend (T) -> IoResult<R>): IoResult<R> {
    return when (this) {
        is IoResult.Success<T> -> mapper(data)
        is IoResult.Error -> this
    }
}

fun <T: Any> IoResult<T>.fix(orDefault: T): T {
    return when (this) {
        is IoResult.Success<T> -> this.data
        is IoResult.Error -> orDefault
    }
}

suspend inline fun <T : Any> IoResult<T>.filter(
    crossinline predicate: suspend (T) -> Boolean
): IoResult<T>? {

    return when (this) {
        is IoResult.Success<T> -> {
            if (predicate(data)) {
                this
            } else {
                null
            }
        }
        is IoResult.Error -> this
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T: Any> IoResult<T>.filterSuccess(): IoResult.Success<T>? {
    if (this is IoResult.Success<T>) {
        return this
    }
    return null
}

fun <T: Any> IoResult<T>?.orDefault(default: T): T {
    if (this == null) {
        return default
    }
    return fix(default)
}

suspend fun <T: Any, R: Any, S: Any> IoResult<T>.combine(
    other: IoResult<R>, combiner: suspend(T, R) -> S
) : IoResult<S> {
    if (this is IoResult.Success && other is IoResult.Success) {
        return IoResult.Success(combiner(this.data, other.data))
    }
    val first = (this as? IoResult.Error)?.extractError()
    val second = (other as? IoResult.Error)?.extractError()
    return IoResult.Error.Generic(
        Exception("first=$first, second=$second")
    )
}

private fun IoResult.Error.extractError(): String {
    return when (this) {
        is IoResult.Error.NetworkError -> "no network"
        is IoResult.Error.TooManyRequests -> "too many request"
        is IoResult.Error.Generic -> this.exception.extractStackTrace
    }
}

private val Throwable.extractStackTrace: String
    get() {
        val sw = StringWriter()
        printStackTrace(PrintWriter(sw))
        return sw.toString()
    }