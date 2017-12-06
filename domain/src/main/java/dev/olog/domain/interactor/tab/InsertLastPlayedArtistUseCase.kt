package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Artist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
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