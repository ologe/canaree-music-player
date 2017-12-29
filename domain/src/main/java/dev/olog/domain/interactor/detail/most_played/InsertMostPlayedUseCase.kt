package dev.olog.domain.interactor.detail.most_played

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Completable
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        return when (mediaId.category){
            MediaIdCategory.FOLDER -> folderGateway.insertMostPlayed(mediaId)
            MediaIdCategory.PLAYLIST -> playlistGateway.insertMostPlayed(mediaId)
            MediaIdCategory.GENRE -> genreGateway.insertMostPlayed(mediaId)
            else -> Completable.complete()
        }
    }

}