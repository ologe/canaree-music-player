package dev.olog.core

import kotlinx.coroutines.CancellationException
import java.io.IOException

sealed interface IoResult<out T> {

    data class Success<T>(val value: T) : IoResult<T>

    sealed interface Failure : IoResult<Nothing> {

        val exception: Throwable
            get() = when (this) {
                is Http -> this
                is Network -> this
                is Unknown -> this
            }

        class Http(
            private val status: Int,
            message: String,
        ) : RuntimeException(message), Failure {

            val isServerError: Boolean
                get() = status in 500..599

        }

        class Network(cause: Throwable) : IOException(cause), Failure
        class Unknown(cause: Throwable) : RuntimeException(cause), Failure
    }


    companion object {

        @Suppress("LiftReturnOrAssignment")
        inline fun <T> of(block: () -> T): IoResult<T> {
            try {
                return Success(block())
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Error) {
                throw ex
            } catch (ex: Throwable) {
                return Failure.Unknown(ex)
            }
        }

    }

}

inline fun <T, R> IoResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (Throwable) -> R,
): R = when (this) {
    is IoResult.Success -> onSuccess(value)
    is IoResult.Failure -> onError(exception)
}

inline fun <T, R> IoResult<T>.map(mapper: (T) -> R): IoResult<R> = when (this) {
    is IoResult.Success -> IoResult.of { mapper(value) }
    is IoResult.Failure.Http -> this
    is IoResult.Failure.Network -> this
    is IoResult.Failure.Unknown -> this
}

inline fun <T, R> IoResult<T>.flatMap(mapper: (T) -> IoResult<R>): IoResult<R> = when (this) {
    is IoResult.Success -> {
        try {
            mapper(value)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Error) {
            throw ex
        } catch (ex: Throwable) {
            IoResult.Failure.Unknown(ex)
        }
    }
    is IoResult.Failure.Http -> this
    is IoResult.Failure.Network -> this
    is IoResult.Failure.Unknown -> this
}

inline fun <T> IoResult<T>.doOnSuccess(block: (T) -> Unit): IoResult<T> {
    if (this is IoResult.Success) {
        block(value)
    }
    return this
}

fun <T> IoResult<T>.getOrNull(): T? = when (this) {
    is IoResult.Success -> value
    is IoResult.Failure -> null
}