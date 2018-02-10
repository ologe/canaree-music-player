package dev.olog.msc.domain.interactor.detail.most.played

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
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