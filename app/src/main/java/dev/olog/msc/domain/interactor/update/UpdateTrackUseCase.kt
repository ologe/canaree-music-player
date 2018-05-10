package dev.olog.msc.domain.interactor.update

import android.content.Intent
import android.net.Uri
import androidx.work.Data
import androidx.work.Worker
import dev.olog.msc.app.app
import dev.olog.msc.catchNothing
import dev.olog.msc.domain.interactor.base.WorkerUseCaseWithParam
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(): WorkerUseCaseWithParam() {

    companion object {
        private const val TAG = "DeleteLastFmTrackUseCase"
        const val PATH = "$TAG.path"

        const val NEW_TITLE = "$TAG.title"
        const val NEW_ARTIST = "$TAG.artist"
        const val NEW_ALBUM = "$TAG.album"
        const val NEW_GENRE = "$TAG.genre"
        const val NEW_YEAR = "$TAG.year"
        const val NEW_DISC_NO = "$TAG.disc"
        const val NEW_TRACK_NO = "$TAG.track"
    }

    override fun buildUseCase(input: Data): Worker.WorkerResult {
        val path = input.getString(PATH, "")
        val file = File(path)

        update(file, input)
        notifyMediaStore(file)

        return Worker.WorkerResult.SUCCESS
    }

    private fun update(file: File, input: Data){
        val audioFile = AudioFileIO.read(file)

        val tag = audioFile.tagOrCreateAndSetDefault

        catchNothing { tag.setField(FieldKey.TITLE, input.getString(NEW_TITLE, "")) }
        catchNothing { tag.setField(FieldKey.ARTIST, input.getString(NEW_ARTIST, "")) }
        catchNothing { tag.setField(FieldKey.ALBUM_ARTIST, input.getString(NEW_ARTIST, "")) }
        catchNothing { tag.setField(FieldKey.ALBUM, input.getString(NEW_ALBUM, "")) }
        catchNothing { tag.setField(FieldKey.GENRE, input.getString(NEW_GENRE, "")) }
        catchNothing { tag.setField(FieldKey.YEAR, input.getString(NEW_YEAR, "")) }
        catchNothing { tag.setField(FieldKey.DISC_NO, input.getString(NEW_DISC_NO, "")) }
        catchNothing { tag.setField(FieldKey.TRACK, input.getString(NEW_TRACK_NO, "")) }

        audioFile.commit()
    }

    private fun notifyMediaStore(file: File){
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(file)
        app.sendBroadcast(intent)
    }


}