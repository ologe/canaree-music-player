package dev.olog.presentation.edit.domain

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
                increaseImageVersion(param.mediaId)
                saveImage(param.mediaId, param.image)
                updateMediaStore(id, param.isPodcast)
            }

            @Suppress("DEPRECATION")
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(File((param.path)))
            context.sendBroadcast(intent)
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

    private fun saveImage(mediaId: MediaId, image: SaveImageType) = when(image) {
        is SaveImageType.Skip -> {
            // do nothing
        }
        is SaveImageType.Original -> {
            // remove override image
            imageVersionGateway.setCurrentVersion(mediaId, 0)
            gateway.setForTrack(mediaId.resolveId, null)
        }
        is SaveImageType.Stylized -> {
            val bitmap = image.bitmap
            val folder = ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.SONG)
            val dest = File(folder, "${mediaId.resolveId}_stylized.webp") // override
            val out = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.WEBP, 90, out)
            bitmap.recycle()
            out.close()
            gateway.setForTrack(mediaId.resolveId, dest.path)
        }
    }

    private fun updateMediaStore(id: Long, isPodcast: Boolean?) {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        val values = ContentValues(2).apply {
            isPodcast?.let {
                put(MediaStore.Audio.Media.IS_PODCAST, it)
            }
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }
        context.contentResolver.update(uri, values, null, null)
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

    class Data(
        @JvmField
        val mediaId: MediaId?,
        @JvmField
        val path: String,
        @JvmField
        val image: SaveImageType,
        @JvmField
        val fields: Map<FieldKey, String>,
        @JvmField
        val isPodcast: Boolean?
    )

}