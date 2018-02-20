package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.repository.CommonQuery
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import io.reactivex.Flowable
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

class PlaylistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val imagesThreadPool: ImagesThreadPool

) {

    fun execute(playlists: List<Playlist>) : Flowable<*> {
        return Flowable.fromIterable(playlists)
                .observeOn(imagesThreadPool.scheduler)
                .map {
                    val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", it.id)
                    Pair(it, CommonQuery.extractAlbumIdsFromSongs(ctx.contentResolver, uri))
                }
                .map { (playlist, albumsId) -> try {
                    makeImage(playlist, albumsId)
                } catch (ex: Exception){ false }
                }
                .buffer(10)
                .filter { it.reduce { acc, curr -> acc || curr } }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }

    private fun makeImage(playlist: Playlist, albumsId: List<Long>) : Boolean {
        assertBackgroundThread()
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.PLAYLIST)
        return MergedImagesCreator.makeImages2(ctx, albumsId, folderName, "${playlist.id}")
    }

}