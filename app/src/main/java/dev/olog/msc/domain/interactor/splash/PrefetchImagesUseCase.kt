package dev.olog.msc.domain.interactor.splash

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import javax.inject.Inject

class PrefetchImagesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val artistGateway: ArtistGateway,
        private val genreGateway: GenreGateway

): CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return createAll().toCompletable()
    }

    private fun createAll() : Single<Any> {
        return Singles.zip(
                folderGateway.createImages(),
                playlistGateway.createImages(),
                artistGateway.createImages(),
                genreGateway.createImages(), { _,_,_, _ -> Unit } )
    }
}