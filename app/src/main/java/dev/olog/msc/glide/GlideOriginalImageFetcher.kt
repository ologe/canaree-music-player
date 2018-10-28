package dev.olog.msc.glide

import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.TagException
import java.io.*

private val FALLBACKS = arrayOf("cover.jpg", "album.jpg", "folder.jpg", "cover.png", "album.png", "folder.png")


class GlideOriginalImageFetcher(
        private val mediaId: MediaId,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway

) : DataFetcher<InputStream> {

    private var disposable: Disposable? = null

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val id = getId()
        if (id == -1L){
           callback.onLoadFailed(Exception("item not found"))
            return
        }

        disposable = when {
            mediaId.isLeaf && !mediaId.isPodcast -> songGateway.getByParam(id).map { it.path }
            mediaId.isLeaf && mediaId.isPodcast -> podcastGateway.getByParam(id).map { it.path }
            mediaId.isAlbum -> songGateway.getByAlbumId(id).map { it.path }
            mediaId.isPodcastAlbum -> podcastGateway.getByAlbumId(id).map { it.path }
            else -> Observable.error(IllegalArgumentException("not a valid media id=$mediaId"))
        }.firstOrError().subscribe({
            try {
                val stream = loadImage(it)
                callback.onDataReady(stream)
            } catch (ex: Exception){
                callback.onLoadFailed(ex)
            }
        }, {
            it.printStackTrace()
            callback.onLoadFailed(Exception(it))
        })
    }

    private fun loadImage(path: String): InputStream? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            val picture = retriever.embeddedPicture
            if (picture != null) {
                ByteArrayInputStream(picture)
            } else {
                fallback(path)
            }
        } finally {
            retriever.release()
        }
    }

    private fun fallback(path: String): InputStream? {
        try {
            val mp3File = MP3File(path)
            if (mp3File.hasID3v2Tag()){
                val art = mp3File.tag.firstArtwork
                if (art != null){
                    val data = art.binaryData
                    return ByteArrayInputStream(data)
                }
            }
        } catch (ex: ReadOnlyFileException){}
        catch (ex: InvalidAudioFrameException){}
        catch (ex: TagException){ }
        catch (ex: IOException){}

        val parent = File(path).parentFile
        for (fallback in FALLBACKS) {
            val cover = File(parent, fallback)
            if (cover.exists()) {
                return FileInputStream(cover)
            }
        }
        return null
    }

    private fun getId(): Long {
        var trackId = -1L
        if (mediaId.isLeaf){
            trackId = mediaId.leaf!!
        } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
            trackId = mediaId.categoryValue.toLong()
        }
        return trackId
    }

    override fun cleanup() {
        disposable.unsubscribe()
    }

    override fun cancel() {
        disposable.unsubscribe()
    }

}