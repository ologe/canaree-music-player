package dev.olog.domain.interactor.splash

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCase
import io.reactivex.Completable
import javax.inject.Inject

class PrefetchImagesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val artistGateway: ArtistGateway,
        private val genreGateway: GenreGateway

): CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return Completable.fromCallable { createAll() }
    }

    private fun createAll(){
        folderGateway.createImages()
        playlistGateway.createImages()
        artistGateway.createImages()
        genreGateway.createImages()
    }
}