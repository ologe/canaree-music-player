package dev.olog.presentation.edit.domain

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import dev.olog.presentation.PresentationId
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    private val context: Context
) {

    operator fun invoke(param: Data) {
        try {
            val file = File(param.path)
            val audioFile = AudioFileIO.read(file)
            val tag = retrieveTag(audioFile)
            updateTagFields(tag, param)
            audioFile.commit()

            val id = param.mediaId?.id?.toLong()

            if (id != null) {
                updateMediaStore(id, param.isPodcast)
            }

            @Suppress("DEPRECATION")
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(File((param.path)))
            context.sendBroadcast(intent)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun retrieveTag(audioFile: AudioFile): Tag {
        return audioFile.tagOrCreateAndSetDefault
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
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    data class Data(
        val mediaId: PresentationId.Track?,
        val path: String,
        val fields: Map<FieldKey, String>,
        val isPodcast: Boolean?
    )

}