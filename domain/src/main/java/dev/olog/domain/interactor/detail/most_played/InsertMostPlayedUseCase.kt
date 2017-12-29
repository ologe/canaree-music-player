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

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Completable {
        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> folderGateway.insertMostPlayed(mediaId)
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistGateway.insertMostPlayed(mediaId)
            MediaIdHelper.MEDIA_ID_BY_GENRE -> genreGateway.insertMostPlayed(mediaId)
            else -> Completable.complete()
        }
    }

}