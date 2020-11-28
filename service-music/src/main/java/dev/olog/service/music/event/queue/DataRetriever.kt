package dev.olog.service.music.event.queue

import dev.olog.service.music.model.MediaEntity
import dev.olog.shared.android.BundleDictionary
import java.net.URI

internal interface DataRetriever {

    suspend fun getLastQueue(): List<MediaEntity>

    suspend fun getFromMediaId(
        mediaId: String,
        extras: BundleDictionary,
    ): List<MediaEntity>

    suspend fun getFromSearch(
        query: String?,
        extras: BundleDictionary,
    ): List<MediaEntity>

    suspend fun getFromUri(
        uri: URI,
        extras: BundleDictionary,
    ): List<MediaEntity>

}