package dev.olog.msc.domain.interactor.offline.lyrics

import dev.olog.msc.domain.entity.OfflineLyrics
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.OfflineLyricsGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
        executors: IoScheduler,
        private val gateway: OfflineLyricsGateway,
        private val getSongUseCase: GetSongUseCase

) : CompletableUseCaseWithParam<OfflineLyrics>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(offlineLyrics: OfflineLyrics): Completable {
        return getSongUseCase.execute(MediaId.songId(offlineLyrics.trackId))
                .firstOrError()
                .flatMapCompletable { saveLyricsOnMetadata(it, offlineLyrics.lyrics) }
                .andThen(gateway.saveLyrics(offlineLyrics))
                .onErrorResumeNext { gateway.saveLyrics(offlineLyrics) }
    }

    fun saveLyricsOnMetadata(song: Song, lyrics: String): Completable {
        return Completable.create {
            val file = File(song.path)
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagAndConvertOrCreateAndSetDefault
            tag.setField(FieldKey.LYRICS, lyrics)
            audioFile.commit()

            it.onComplete()
        }
    }

}