package dev.olog.core

import androidx.annotation.CheckResult
import java.io.IOException
import java.util.concurrent.CancellationException

sealed class Try<out T> {

    data class Success<out T>(val value: T) : Try<T>()
    data class Failure(val throwable: Throwable) : Try<Nothing>()

    companion object {

        @CheckResult
        fun <T> success(value: T): Try<T> = Success(value)

        @CheckResult
        fun <T> failure(throwable: Throwable): Try<T> = Failure(throwable)

        @CheckResult
        inline fun <T> of(crossinline block: () -> T): Try<T> = try {
            success(block())
        } catch (error: Error) {
            throw error
        } catch (ce: CancellationException) {
            throw ce
        } catch (throwable: Throwable) {
            failure(throwable)
        }

    }

}

@CheckResult
fun <T> Try<T>.getOrNull(): T? = when (this) {
    is Try.Success -> value
    is Try.Failure -> {
        if (throwable !is IOException) {
            throwable.printStackTrace()
        }
        null
    }
}

@CheckResult
inline fun <T, R> Try<T>.map(crossinline transform: (T) -> R): Try<R> = when (this) {
    is Try.Success -> Try.of { transform(value) }
    is Try.Failure -> Try.failure(throwable)
}