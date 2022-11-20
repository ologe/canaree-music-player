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
import dev.olog.image.provider.fetcher.internal.RemoteSongFetcher
import dev.olog.image.provider.loader.LeafKey
import java.io.InputStream

internal class SongDataFetcher @AssistedInject constructor(
    @Assisted private val mediaId: MediaId,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val originalImageFetcher: OriginalImageFetcher,
    private val remoteFetcher: RemoteSongFetcher,
) : CoroutinesDataFetcher() {

    @AssistedFactory
    interface Factory {
        fun create(mediaId: MediaId): SongDataFetcher
    }

    override suspend fun load(priority: Priority): ImageRetrieverResult<InputStream> {
        if (mediaId.isPodcast) {
            return loadPodcast()
        }
        return loadSong(priority)
    }

    private suspend fun loadSong(priority: Priority): ImageRetrieverResult<InputStream> {
        val id = mediaId.leaf ?: error("id can not be null for podcasts")
        val song = songGateway.getByParam(id) ?: return ImageRetrieverResult.NotFound

        val originalImageStream = originalImageFetcher.load(song)
        if (originalImageStream != null) {
            return ImageRetrieverResult.Success(originalImageStream)
        }
        return remoteFetcher.load(priority, song)
    }

    private fun loadPodcast(): ImageRetrieverResult<InputStream> {
        val id = mediaId.leaf ?: error("id can not be null for podcasts")
        val podcast = podcastGateway.getByParam(id) ?: return ImageRetrieverResult.NotFound
        return ImageRetrieverResult(originalImageFetcher.load(podcast))
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun createCacheKey(): Key {
        return LeafKey(mediaId)
    }
}