package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.olog.core.MediaId
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.image.provider.fetcher.internal.CoroutinesDataFetcher
import dev.olog.image.provider.internal.ImageLoaderPreferences
import dev.olog.image.provider.loader.MergedImageKey
import dev.olog.image.provider.merger.ImageMergerFactory
import java.io.InputStream

internal class FolderDataFetcher @AssistedInject constructor(
    @Assisted private val mediaId: MediaId,
    private val folderGateway: FolderGateway,
    private val prefs: ImageLoaderPreferences,
    private val mergedImagesCreator: ImageMergerFactory,
) : CoroutinesDataFetcher() {

    @AssistedFactory
    interface Factory {
        fun create(mediaId: MediaId): FolderDataFetcher
    }

    override suspend fun load(priority: Priority): ImageRetrieverResult<InputStream> {
        if (!prefs.canAutoCreateImages()) {
            return ImageRetrieverResult.NotFound
        }

        val path = mediaId.categoryValue
        val albumsIds = folderGateway.getTrackListByParam(path).map { it.albumId }

        return mergedImagesCreator.create(albumIds = albumsIds)
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun createCacheKey(): Key {
        val path = mediaId.categoryValue
        val albumsIds = folderGateway.getTrackListByParam(path).map { it.albumId }
        return MergedImageKey(albumsIds.toSet(), mediaId)
    }
}