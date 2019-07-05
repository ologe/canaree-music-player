package dev.olog.offlinelyrics.domain

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.entity.track.Song
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
    executors: IoScheduler,
    private val gateway: OfflineLyricsGateway,
    private val getSongUseCase: SongGateway

) : CompletableUseCaseWithParam<OfflineLyrics>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(offlineLyrics: OfflineLyrics): Completable {
        return getSongUseCase.observeByParam(offlineLyrics.trackId).map { it!! }
            .asObservable()
            .firstOrError()
            .flatMapCompletable { saveLyricsOnMetadata(it, offlineLyrics.lyrics) }
            .andThen(gateway.saveLyrics(offlineLyrics))
            .onErrorResumeNext { gateway.saveLyrics(offlineLyrics) }
    }

    fun saveLyricsOnMetadata(song: Song, lyrics: String): Completable {
        return Completable.create {
            updateTrackMetadata(song.path, lyrics)
            updateFileIfAny(song.path, lyrics)

            it.onComplete()
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