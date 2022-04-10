package dev.olog.presentation.edit.domain

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    @ApplicationContext private val context: Context
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

    data class Data(
        val mediaId: MediaId?,
        val path: String,
        val fields: Map<FieldKey, String>,
        val isPodcast: Boolean?
    )

}