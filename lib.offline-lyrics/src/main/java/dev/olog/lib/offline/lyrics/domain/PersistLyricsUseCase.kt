package dev.olog.lib.offline.lyrics.domain

import dev.olog.domain.entity.track.Song
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PersistLyricsUseCase @Inject constructor() {

    operator fun invoke(song: Song, lyrics: String) {
        try {
            updateTrackMetadata(song.path, lyrics)
            updateFileIfAny(song.path, lyrics)
        } catch (ex: Exception){
            Timber.e(ex)
        }
    }

    private fun updateTrackMetadata(path: String, lyrics: String) {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagAndConvertOrCreateAndSetDefault
        tag.setField(FieldKey.LYRICS, lyrics)
        audioFile.commit()
    }

    private fun updateFileIfAny(path: String, lyrics: String) {
        val file = File(path)
        val fileName = file.nameWithoutExtension
        val lyricsFile = File(file.parentFile, "$fileName.lrc")

        if (lyricsFile.exists()) {
            lyricsFile.printWriter().use { out ->
                val lines = lyrics.split("\n")
                lines.forEach {
                    out.println(it)
                }
            }
        }
    }

}