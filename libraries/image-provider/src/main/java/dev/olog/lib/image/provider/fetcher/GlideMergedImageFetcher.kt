package dev.olog.lib.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.lib.image.provider.creator.ImagesFolderUtils
import dev.olog.lib.image.provider.creator.MergedImagesCreator
import dev.olog.lib.image.provider.executor.GlideScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class GlideMergedImageFetcher(
    private val context: Context,
    private val mediaId: MediaId,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway
) : DataFetcher<InputStream> {

    private val scope: CoroutineScope = GlideScope()

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        scope.launch {
            try {
                val inputStream = when {
                    mediaId.isFolder -> makeFolderImage(mediaId.categoryValue)
                    mediaId.isGenre -> makeGenreImage(mediaId.categoryId)
                    else -> makePlaylistImage(mediaId.categoryId)
                }
                callback.onDataReady(inputStream)
            } catch (ex: Throwable){
                callback.onLoadFailed(RuntimeException(ex))
            }
        }
    }


    private suspend fun makeFolderImage(folder: String): InputStream? {
//        val folderImage = ImagesFolderUtils.forFolder(context, dirPath) --contains current image
        val albumsId = folderGateway.getTrackListByParam(folder).map { it.albumId }

        val folderName = ImagesFolderUtils.FOLDER
        val normalizedPath = folder.replace(File.separator, "")

        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = normalizedPath
        )
        return file?.inputStream()
    }

    private suspend fun makeGenreImage(genreId: Long): InputStream? {
//        ImagesFolderUtils.forGenre(context, id) --contains current image

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

//        ImagesFolderUtils.forPlaylist(context, id) --contains current image
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
        scope.cancel()
    }

}