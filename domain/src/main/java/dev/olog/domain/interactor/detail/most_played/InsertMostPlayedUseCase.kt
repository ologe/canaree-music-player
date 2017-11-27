package dev.olog.domain.interactor.detail.most_played

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : CompletableUseCaseWithParam<String>(scheduler) {

    override fun buildUseCaseObservable(param: String): Completable {
        val category = MediaIdHelper.extractCategory(param)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> folderGateway.insertMostPlayed(param)
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistGateway.insertMostPlayed(param)
            MediaIdHelper.MEDIA_ID_BY_GENRE -> genreGateway.insertMostPlayed(param)
            else -> Completable.complete()
        }
    }

}