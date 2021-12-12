package dev.olog.feature.offline.lyrics.domain

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.track.SongGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
    private val gateway: OfflineLyricsGateway,
    private val songGateway: SongGateway

)  {

    suspend operator fun invoke(playableId: Long, text: String) = withContext(Dispatchers.IO){
        val song = songGateway.getByParam(playableId)
        if (song != null){
            saveLyricsOnMetadata(song, text)
        }
        gateway.saveLyrics(playableId, text)
    }

    suspend fun saveLyricsOnMetadata(song: Song, lyrics: String) {
        try {
            updateTrackMetadata(song.path, lyrics)
            updateFileIfAny(song.path, lyrics)
        } catch (ex: Throwable){
            ex.printStackTrace()
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