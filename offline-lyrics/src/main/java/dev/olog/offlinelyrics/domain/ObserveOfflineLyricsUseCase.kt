package dev.olog.offlinelyrics.domain

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.base.FlowUseCaseWithParam
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
                val song = songGateway.getByParam(param) ?: return@map lyrics
                try {
                    getLyricsFromMetadata(song)
                } catch (ex: Exception) {
                    lyrics
                }
            }.flowOn(Dispatchers.IO)
    }

    private fun getLyricsFromMetadata(song: Song): String {
        val file = File(song.path)

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