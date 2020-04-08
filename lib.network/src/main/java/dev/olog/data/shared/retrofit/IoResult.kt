package dev.olog.data.shared.retrofit

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