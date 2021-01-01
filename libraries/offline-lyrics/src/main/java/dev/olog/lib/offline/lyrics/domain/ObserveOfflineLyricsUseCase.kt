package dev.olog.lib.offline.lyrics.domain

import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
    private val songGateway: SongGateway,
    private val gateway: OfflineLyricsGateway

) : FlowUseCaseWithParam<String, Long>() {

    override fun buildUseCase(param: Long): Flow<String> {
        return gateway.observeLyrics(param)
            .map { lyrics ->
                mapLyrics(param, lyrics)
            }.flowOn(Dispatchers.IO)
    }

    private suspend fun mapLyrics(id: Long, lyrics: String): String {
        val track = songGateway.getByParam(id) ?: return lyrics
        try {
            return getLyricsFromMetadata(track)
        } catch (ex: Throwable) {
            return lyrics
        }
    }

    private fun getLyricsFromMetadata(track: Track): String {
        val file = File(track.path)

        val fileName = file.nameWithoutExtension
        val lyricsFile = File(file.parentFile, "$fileName.lrc")

        if (lyricsFile.exists()) {
            return lyricsFile.bufferedReader().use { it.readText() }
        }

        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagAndConvertOrCreateAndSetDefault
        return tag.getFirst(FieldKey.LYRICS)
    }

}