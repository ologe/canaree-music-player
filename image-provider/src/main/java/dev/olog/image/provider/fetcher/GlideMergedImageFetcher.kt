package dev.olog.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.image.provider.creator.ImagesFolderUtils
import dev.olog.image.provider.creator.MergedImagesCreator
import dev.olog.image.provider.executor.GlideScope
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream

class GlideMergedImageFetcher(
    private val context: Context,
    private val mediaId: MediaId.Category,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway
) : DataFetcher<InputStream>, CoroutineScope by GlideScope() {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        launch {
            try {
                val inputStream = when (mediaId.category) {
                    MediaIdCategory.FOLDERS -> makeFolderImage(mediaId.categoryId)
                    MediaIdCategory.GENRES -> makeGenreImage(mediaId.categoryId.toLong())
                    MediaIdCategory.PLAYLISTS -> makePlaylistImage(mediaId.categoryId.toLong())
                    else -> throwNotHandled(mediaId)
                }
                callback.onDataReady(inputStream)
            } catch (ex: Exception){
                Timber.w(ex)
                callback.onLoadFailed(RuntimeException(ex))
            }
        }
    }


    private suspend fun makeFolderImage(folderId: String): InputStream? {
        val albumsId = folderGateway.getTrackListByParam(folderId).map { it.albumId }

        val folderName = ImagesFolderUtils.FOLDER

        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = folderId
        )
        return file?.inputStream()
    }

    private suspend fun makeGenreImage(genreId: Long): InputStream? {
        val albumsId = genreGateway.getTrackListByParam(genreId).map { it.albumId }

        val folderName = ImagesFolderUtils.GENRE
        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = "$genreId"
        )
        return file?.inputStream()
    }

    private suspend fun makePlaylistImage(playlistId: Long): InputStream? {
        if (AutoPlaylist.isAutoPlaylist(playlistId)){
            return null
        }

        val albumsId = playlistGateway.getTrackListByParam(playlistId).map { it.albumId }

        val folderName = ImagesFolderUtils.PLAYLIST
        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = "$playlistId"
        )
        return file?.inputStream()
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cleanup() {

    }

    override fun cancel() {
        cancel(null)
    }

}