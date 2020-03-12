package dev.olog.offlinelyrics.domain

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
    private val gateway: OfflineLyricsGateway,
    private val trackGateway: TrackGateway,
    private val schedulers: Schedulers

)  {

    suspend operator fun invoke(offlineLyrics: OfflineLyrics) = withContext(schedulers.io){
        val song = trackGateway.getByParam(offlineLyrics.trackId)
        if (song != null){
            saveLyricsOnMetadata(song, offlineLyrics.lyrics)
        }
        gateway.saveLyrics(offlineLyrics)
    }

    private fun saveLyricsOnMetadata(song: Song, lyrics: String) {
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