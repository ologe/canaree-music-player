package dev.olog.core.interactor.edit

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.UsedImageGateway
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: UsedImageGateway

) {

    operator fun invoke(param: Data) {
        try {
            val file = File(param.path)
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            try {
                tag.setEncoding("UTF-8")
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

            for (field in param.fields) {
                try {
                    tag.setField(field.key, field.value)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
            }

            audioFile.commit()

            if (param.id != null) {
                gateway.setForTrack(param.id, param.image)
                updateDataModified(param.id) // TODO image for some reasong is loading for another id
            }

        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    private fun updateDataModified(id: Long){
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues(1).apply {
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }
        context.contentResolver.update(uri, values, "${BaseColumns._ID} = ?", arrayOf("$id"))
    }

    data class Data(
        val id: Long?,
        val path: String,
        val image: String?,
        val fields: Map<FieldKey, String>
    )

}