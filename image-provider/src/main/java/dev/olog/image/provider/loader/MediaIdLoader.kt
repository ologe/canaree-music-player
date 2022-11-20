package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.image.provider.fetcher.*
import dev.olog.image.provider.internal.isDummyStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Provider

internal class MediaIdLoader @Inject constructor(
    private val songFetcher: SongDataFetcher.Factory,
    private val albumFetcher: AlbumDataFetcher.Factory,
    private val artistFetcher: ArtistDataFetcher.Factory,
    private val folderFetcher: FolderDataFetcher.Factory,
    private val genreFetcher: GenreDataFetcher.Factory,
    private val playlistFetcher: PlaylistDataFetcher.Factory,
    private val diskCache: DiskCache,
) : ModelLoader<MediaId, InputStream> {

    override fun handles(model: MediaId): Boolean = true

    override fun buildLoadData(
        model: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val fetcher = when {
            model.isLeaf -> songFetcher.create(model)
            model.isAlbum -> albumFetcher.create(model)
            model.isArtist -> artistFetcher.create(model)
            model.isFolder -> folderFetcher.create(model)
            model.isGenre -> genreFetcher.create(model)
            model.isPlaylist -> playlistFetcher.create(model)
            else -> return null
        }
        val sourceKey = fetcher.createCacheKey()
        if (hasDummyCache(sourceKey)) {
            return null
        }

        return ModelLoader.LoadData(
            sourceKey,
            fetcher
        )
    }

    private fun hasDummyCache(sourceKey: Key): Boolean {
        val cacheKey = DataCacheKey(sourceKey)
        val cache = diskCache.get(cacheKey) ?: return false
        return cache.isDummyStream()
    }

    internal class Factory @Inject constructor(
        private val loader: Provider<MediaIdLoader>,
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return loader.get()
        }

        override fun teardown() {

        }
    }

}