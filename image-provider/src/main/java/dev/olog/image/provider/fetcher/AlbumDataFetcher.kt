package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.olog.core.MediaId
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.fetcher.internal.CoroutinesDataFetcher
import dev.olog.image.provider.fetcher.internal.OriginalImageFetcher
import dev.olog.image.provider.fetcher.internal.RemoteAlbumFetcher
import dev.olog.image.provider.loader.CategoryKey
import java.io.InputStream

internal class AlbumDataFetcher @AssistedInject constructor(
    @Assisted private val mediaId: MediaId,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val originalImageFetcher: OriginalImageFetcher,
    private val remoteFetcher: RemoteAlbumFetcher,
) : CoroutinesDataFetcher() {

    @AssistedFactory
    interface Factory {
        fun create(mediaId: MediaId): AlbumDataFetcher
    }

    override suspend fun load(priority: Priority): ImageRetrieverResult<InputStream> {
        if (mediaId.isPodcastAlbum) {
            return loadPodcastAlbum()
        }
        return loadAlbum(priority)
    }

    private fun loadPodcastAlbum(): ImageRetrieverResult<InputStream> {
        val id = mediaId.categoryId
        val podcast = podcastGateway.getByAlbumId(id) ?: return ImageRetrieverResult.NotFound
        return ImageRetrieverResult(originalImageFetcher.load(podcast))
    }

    private suspend fun loadAlbum(priority: Priority): ImageRetrieverResult<InputStream> {
        val id = mediaId.categoryId
        val song = songGateway.getByAlbumId(id) ?: return ImageRetrieverResult.NotFound

        val originalImageStream = originalImageFetcher.load(song)
        if (originalImageStream != null) {
            return ImageRetrieverResult.Success(originalImageStream)
        }
        return remoteFetcher.load(priority, mediaId)
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.REMOTE
    override fun createCacheKey(): Key = CategoryKey(mediaId)
}