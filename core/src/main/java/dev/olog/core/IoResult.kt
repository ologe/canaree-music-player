package dev.olog.core

import kotlinx.coroutines.CancellationException

sealed class IoResult<out T> {

    data class Success<T>(val value: T) : IoResult<T>()

    sealed class Failure : IoResult<Nothing>() {

        abstract val exception: Throwable

        data class Http(
            val status: Int,
            val message: String,
            override val exception: Throwable
        ) : Failure() {

            val isServerError: Boolean
                get() = status in 500..599

        }

        data class Network(override val exception: Throwable) : Failure()
        data class Unknown(override val exception: Throwable) : Failure()
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
    is IoResult.Failure.Http -> IoResult.Failure.Http(status, message, exception)
    is IoResult.Failure.Network -> IoResult.Failure.Network(exception)
    is IoResult.Failure.Unknown -> IoResult.Failure.Unknown(exception)
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
    is IoResult.Failure.Http -> IoResult.Failure.Http(status, message, exception)
    is IoResult.Failure.Network -> IoResult.Failure.Network(exception)
    is IoResult.Failure.Unknown -> IoResult.Failure.Unknown(exception)
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