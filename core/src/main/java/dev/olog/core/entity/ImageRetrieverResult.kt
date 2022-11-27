package dev.olog.core.entity

import kotlinx.coroutines.Deferred

sealed class ImageRetrieverResult<out T> {

    data class Success<T>(val data: T) : ImageRetrieverResult<T>()
    object NotFound : ImageRetrieverResult<Nothing>()
    data class Error(val exception: Throwable) : ImageRetrieverResult<Nothing>()

    companion object {

        operator fun <T : Any> invoke(data: T?): ImageRetrieverResult<T> {
            if (data == null) {
                return NotFound
            }
            return Success(data)
        }

    }

}

suspend fun <T> Deferred<ImageRetrieverResult<T>>.merge(second: Deferred<ImageRetrieverResult<T>>): ImageRetrieverResult<T> {
    val firstResult = this.await()
    if (firstResult is ImageRetrieverResult.Success) {
        second.cancel()
        return ImageRetrieverResult.Success(firstResult.data)
    }
    val secondResult = second.await()
    if (secondResult is ImageRetrieverResult.Success) {
        return ImageRetrieverResult.Success(secondResult.data)
    }

    return when {
        // propagate errors first
        firstResult is ImageRetrieverResult.Error && secondResult is ImageRetrieverResult.Error -> firstResult
        firstResult is ImageRetrieverResult.Error -> firstResult
        secondResult is ImageRetrieverResult.Error -> secondResult
        else -> ImageRetrieverResult.NotFound
    }
}