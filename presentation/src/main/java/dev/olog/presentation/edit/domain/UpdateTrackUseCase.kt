package dev.olog.presentation.edit.domain

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.image.provider.creator.ImagesFolderUtils
import dev.olog.presentation.edit.model.SaveImageType
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: UsedImageGateway,
    private val imageVersionGateway: ImageVersionGateway
) {

    operator fun invoke(param: Data) {
        try {
            val file = File(param.path)
            val audioFile = AudioFileIO.read(file)
            val tag = retrieveTag(audioFile)
            updateTagFields(tag, param)
            audioFile.commit()

            val id = param.mediaId?.resolveId

            if (id != null) {
                saveImage(id, param.image)
                increaseImageVersion(param.mediaId)
                updateMediaStore(id, param.isPodcast) // TODO image for some reasong is loading for another id
            }

        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    private fun retrieveTag(audioFile: AudioFile): Tag {

        val tag = audioFile.tagOrCreateAndSetDefault
        try {
            tag.setEncoding("UTF-8")
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        return tag
    }

    private fun saveImage(id: Long, image: SaveImageType) = when(image) {
        is SaveImageType.NotSet -> {
            // do nothing
        }
        is SaveImageType.Original -> {
            // remove override image
            gateway.setForTrack(id, null)
        }
        is SaveImageType.Url -> {
            gateway.setForTrack(id, image.url)
        }
        is SaveImageType.Stylized -> {
            val bitmap = image.bitmap
            val dest = File(ImagesFolderUtils.SONG, "${id}_stylized.webp") // override
            val out = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.WEBP, 90, out)
            bitmap.recycle()
            out.close()
            gateway.setForTrack(id, dest.path)
        }
    }

    private fun updateMediaStore(id: Long, isPodcast: Boolean?) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues(2).apply {
            isPodcast?.let {
                put(MediaStore.Audio.Media.IS_PODCAST, it)
            }
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }
        context.contentResolver.update(uri, values, "${BaseColumns._ID} = ?", arrayOf("$id"))
    }

    private fun updateTagFields(tag: Tag, param: Data){
        for (field in param.fields) {
            try {
                tag.setField(field.key, field.value)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    private fun increaseImageVersion(mediaId: MediaId){
        imageVersionGateway.increaseCurrentVersion(mediaId)
    }

    data class Data(
        val mediaId: MediaId?,
        val path: String,
        val image: SaveImageType,
        val fields: Map<FieldKey, String>,
        val isPodcast: Boolean?
    )

}