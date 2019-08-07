package dev.olog.presentation.edit.domain

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.image.provider.creator.ImagesFolderUtils
import dev.olog.presentation.edit.model.SaveImageType
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UpdateMultipleTracksUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val updateTrackUseCase: UpdateTrackUseCase,
    private val gateway: UsedImageGateway,
    private val imageVersionGateway: ImageVersionGateway

) {

    operator fun invoke(param: Data) {
        try {
            val songList = getSongListByParamUseCase(param.mediaId)
            for (song in songList) {
                updateTrackUseCase(
                    UpdateTrackUseCase.Data(
                        mediaId = null, // set to null because do not want to update track image
                        path = song.path,
                        image = SaveImageType.NotSet,
                        fields = param.fields,
                        isPodcast = null
                    )
                )
            }
            if (param.mediaId.isArtist || param.mediaId.isPodcastArtist) {
                increaseImageVersion(param.mediaId)
                saveArtistImage(param.mediaId.categoryId, param.image)
                updateArtistMediaStore(param.mediaId.categoryId, param.isPodcast)
            } else if (param.mediaId.isAlbum || param.mediaId.isPodcastAlbum) {
                increaseImageVersion(param.mediaId)
                saveAlbumImage(param.mediaId.categoryId, param.image)
                updateAlbumMediaStore(param.mediaId.categoryId, param.isPodcast)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }

    }

    private fun saveAlbumImage(id: Long, image: SaveImageType) = when(image) {
        is SaveImageType.NotSet -> {
            // do nothing
        }
        is SaveImageType.Original -> {
            // remove override image
            gateway.setForAlbum(id, null)
        }
        is SaveImageType.Url -> {
            gateway.setForAlbum(id, image.url)
        }
        is SaveImageType.Stylized -> {
            val bitmap = image.bitmap
            val folder = ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.ALBUM)
            val dest = File(folder, "${id}_stylized.webp") // override
            val out = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.WEBP, 90, out)
            bitmap.recycle()
            out.close()
            gateway.setForAlbum(id, dest.path)
        }
    }

    private fun saveArtistImage(id: Long, image: SaveImageType) = when(image) {
        is SaveImageType.NotSet -> {
            // do nothing
        }
        is SaveImageType.Original -> {
            // remove override image
            gateway.setForArtist(id, null)
        }
        is SaveImageType.Url -> {
            gateway.setForArtist(id, image.url)
        }
        is SaveImageType.Stylized -> {
            val bitmap = image.bitmap
            val folder = ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.ARTIST)
            val dest = File(folder, "${id}_stylized.webp") // override
            val out = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.WEBP, 90, out)
            bitmap.recycle()
            out.close()
            gateway.setForArtist(id, dest.path)
        }
    }

    private fun updateAlbumMediaStore(id: Long, isPodcast: Boolean) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues(1).apply {
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            put(MediaStore.Audio.Media.IS_PODCAST, isPodcast)
        }
        context.contentResolver.update(uri, values, "${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf("$id"))
    }

    private fun updateArtistMediaStore(id: Long, isPodcast: Boolean) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues(1).apply {
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            put(MediaStore.Audio.Media.IS_PODCAST, isPodcast)
        }
        context.contentResolver.update(uri, values, "${MediaStore.Audio.Media.ARTIST_ID} = ?", arrayOf("$id"))
    }

    private fun increaseImageVersion(mediaId: MediaId){
        imageVersionGateway.increaseCurrentVersion(mediaId)
    }

    class Data(
        @JvmField
        val mediaId: MediaId,
        @JvmField
        val image: SaveImageType,
        @JvmField
        val fields: Map<FieldKey, String>,
        @JvmField
        val isPodcast: Boolean
    )

}