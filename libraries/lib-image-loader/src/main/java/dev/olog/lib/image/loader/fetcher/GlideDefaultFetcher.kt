package dev.olog.lib.image.loader.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.shared.throwNotHandled
import java.io.InputStream

class GlideDefaultFetcher (
    private val context: Context,
    private val mediaId: MediaId,
    private val prefsGateway: AppPreferencesGateway,
    private val trackGateway: TrackGateway,
    private val schedulers: Schedulers,
    private val imageRetrieverGateway: ImageRetrieverGateway
) : DataFetcher<InputStream> {

    companion object {
        private const val DEFAULT_THRESHOLD = 600L
        private const val ARTIST_THRESHOLD = 250L
        private val invalidImages = listOf(
            "2a96cbd8b46e442fc41c2b86b821562f.png", // last fm placeholder
            "https://cdns-images.dzcdn.net/images/artist//" // deezer placeholder
        )
    }

    private var remoteFetcher: RemoteDataFetcher? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val local = tryGetLocal()
        if (local != null) {
            callback.onDataReady(local)
            return
        }

        remoteFetcher = RemoteDataFetcher(
            context = context,
            prefsGateway = prefsGateway,
            schedulers = schedulers,
            priority = priority,
            callback = callback,
            threshold = threshold,
            getCached = ::getCached,
            getRemote = ::getRemote
        )
        remoteFetcher!!.loadData()
    }

    private val threshold: Long
        get() {
            if (mediaId is MediaId.Category && mediaId.category == MediaIdCategory.ARTISTS) {
                return ARTIST_THRESHOLD
            }
            return DEFAULT_THRESHOLD
        }

    private suspend fun getCached(): String? {
        if (mediaId is MediaId.Track) {
            return imageRetrieverGateway.getCachedTrack(mediaId.id.toLong())?.image
        }

        return when (mediaId.category) {
            MediaIdCategory.ARTISTS -> {
                imageRetrieverGateway.getCachedArtist(mediaId.categoryId.toLong())?.image
                    ?.takeUnless { it in invalidImages }
            }
            MediaIdCategory.ALBUMS -> imageRetrieverGateway.getCachedAlbum(mediaId.categoryId.toLong())?.image
            else -> throwNotHandled(mediaId)
        }
    }

    private suspend fun getRemote(): String? {
        if (mediaId is MediaId.Track) {
            return imageRetrieverGateway.getTrack(mediaId.id.toLong())?.image
        }

        return when (mediaId.category) {
            MediaIdCategory.ARTISTS -> {
                imageRetrieverGateway.getArtist(mediaId.categoryId.toLong())?.image
                    ?.takeUnless { it in invalidImages }
            }
            MediaIdCategory.ALBUMS -> imageRetrieverGateway.getAlbum(mediaId.categoryId.toLong())?.image
            else -> throwNotHandled(mediaId)
        }
    }

    private fun tryGetLocal(): InputStream? {
        val id = getId().takeIf { it != -1L } ?: return null

        val song: Song? = when (mediaId) {
            is MediaId.Track -> trackGateway.getByParam(id)
            is MediaId.Category -> {
                if (mediaId.category == MediaIdCategory.ALBUMS) {
                    trackGateway.getByAlbumId(id)
                } else {
                    return null
                }
            }
        }
        song ?: return null

        return OriginalImageFetcher.loadImage(context, song)
    }

    private fun getId(): Long {
        return when (mediaId) {
            is MediaId.Track -> mediaId.id.toLong()
            is MediaId.Category -> mediaId.categoryId.toLong()
        }
    }

    override fun cleanup() {
        remoteFetcher?.teardown()
    }

    override fun cancel() {
        remoteFetcher?.teardown()
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.LOCAL

}