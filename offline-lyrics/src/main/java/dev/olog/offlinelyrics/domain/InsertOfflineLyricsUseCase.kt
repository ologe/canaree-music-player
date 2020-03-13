package dev.olog.offlinelyrics.domain

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
    private val gateway: OfflineLyricsGateway,
    private val trackGateway: TrackGateway,
    private val schedulers: Schedulers,
    private val persistLyricsUseCase: PersistLyricsUseCase
)  {

    suspend operator fun invoke(offlineLyrics: OfflineLyrics) = withContext(schedulers.io){
        val song = trackGateway.getByParam(offlineLyrics.trackId)!!
        persistLyricsUseCase(song, offlineLyrics.lyrics)
        gateway.saveLyrics(offlineLyrics)
    }

}