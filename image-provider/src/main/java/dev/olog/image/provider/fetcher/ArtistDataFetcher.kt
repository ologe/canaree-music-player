package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.olog.core.MediaId
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.image.provider.fetcher.internal.CoroutinesDataFetcher
import dev.olog.image.provider.fetcher.internal.RemoteArtistFetcher
import dev.olog.image.provider.loader.CategoryKey
import java.io.InputStream

internal class ArtistDataFetcher @AssistedInject constructor(
    @Assisted private val mediaId: MediaId,
    private val remoteFetcher: RemoteArtistFetcher,
) : CoroutinesDataFetcher() {

    @AssistedFactory
    interface Factory {
        fun create(mediaId: MediaId): ArtistDataFetcher
    }

    override suspend fun load(priority: Priority): ImageRetrieverResult<InputStream> {
        if (mediaId.isPodcastArtist) {
            return ImageRetrieverResult.NotFound
        }
        return remoteFetcher.load(priority, mediaId)
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun createCacheKey(): Key = CategoryKey(mediaId)

}

