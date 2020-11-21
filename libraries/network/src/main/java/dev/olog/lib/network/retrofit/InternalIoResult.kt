package dev.olog.lib.network.retrofit

import dev.olog.lib.network.model.IoResult

internal sealed class InternalIoResult<T> {

    class Success<T>(
        val data: T
    ) : InternalIoResult<T>()

    class ServerError<T>(
        val code: Int,
        val message: String,
    ) : InternalIoResult<T>()

    class NetworkUnavailable<T> : InternalIoResult<T>()

}

internal fun <T> InternalIoResult<T>.toDomain(): IoResult<T> {
    return when (this) {
        is InternalIoResult.Success -> IoResult.Success(data)
        is InternalIoResult.ServerError -> IoResult.ServerError(code, message)
        is InternalIoResult.NetworkUnavailable -> error("cannot convert NetworkUnavailable")
    }
}