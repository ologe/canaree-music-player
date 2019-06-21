package dev.olog.msc.domain.interactor.offline.lyrics

import dev.olog.core.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.OfflineLyricsGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.core.MediaId
import io.reactivex.Observable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
        executors: IoScheduler,
        private val getSongUseCase: GetSongUseCase,
        private val gateway: OfflineLyricsGateway

) : ObservableUseCaseWithParam<String, Long>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(id: Long): Observable<String> {
        return gateway.observeLyrics(id)
                .switchMap { lyrics -> getSongUseCase.execute(MediaId.songId(id))
                        .map { getLyricsFromMetadata(it) }
                        .onErrorReturnItem(lyrics)
                }
    }

    private fun getLyricsFromMetadata(song: Song): String {
        val file = File(song.path)

        val fileName = file.nameWithoutExtension
        val lyricsFile = File(file.parentFile, "$fileName.lrc")

        if (lyricsFile.exists()){
            return lyricsFile.bufferedReader().use { it.readText() }
        }

        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagAndConvertOrCreateAndSetDefault
        return tag.getFirst(FieldKey.LYRICS)
    }

}