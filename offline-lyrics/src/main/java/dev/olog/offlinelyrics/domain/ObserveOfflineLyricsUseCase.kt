package dev.olog.offlinelyrics.domain

import dev.olog.core.entity.track.Song
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.SongGateway
import dev.olog.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
    executors: IoScheduler,
    private val getSongUseCase: SongGateway,
    private val gateway: OfflineLyricsGateway

) : ObservableUseCaseWithParam<String, Long>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(id: Long): Observable<String> {
        return gateway.observeLyrics(id)
            .switchMap { lyrics ->
                getSongUseCase.observeByParam(id).map { it!! }
                    .asObservable()
                    .map { getLyricsFromMetadata(it) }
                    .onErrorReturnItem(lyrics)
            }
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