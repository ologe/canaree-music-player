package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway

): CompletableUseCaseWithParam<Artist>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(artist: Artist): Completable {
        return artistGateway.addLastPlayed(artist)
    }
}