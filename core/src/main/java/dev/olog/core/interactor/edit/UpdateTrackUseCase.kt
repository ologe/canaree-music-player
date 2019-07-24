package dev.olog.core.interactor.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
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
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            for (field in param.fields) {
                try {
                    tag.setField(field.key, field.value)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            audioFile.commit()

            if (param.id != null) {
                gateway.setForTrack(param.id, param.image)
            }


            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(File((param.path)))
            context.sendBroadcast(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    data class Data(
        val id: Long?,
        val path: String,
        val image: String?,
        val fields: Map<FieldKey, String>
    )

}