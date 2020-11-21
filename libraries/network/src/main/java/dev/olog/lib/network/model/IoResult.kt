package dev.olog.lib.network.model

sealed class IoResult<out T> {

    data class Success<T>(
        val data: T
    ) : IoResult<T>()

    data class ServerError(
        val code: Int,
        val message: String,
    ) : IoResult<Nothing>()

    companion object

}

fun <T> IoResult.Companion.just(data: T): IoResult.Success<T> = IoResult.Success(data)