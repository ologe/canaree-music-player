package dev.olog.domain.interactor.detail.most_played

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetMostPlayedSongsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : FlowableUseCaseWithParam<List<Song>, String>(scheduler) {

    override fun buildUseCaseObservable(param: String): Flowable<List<Song>> {
        val category = MediaIdHelper.extractCategory(param)

        return when (category) {
            MediaIdHelper.MEDIA_ID_BY_GENRE -> return genreGateway.getMostPlayed(param)
                    .distinctUntilChanged()
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> return playlistGateway.getMostPlayed(param)
                    .distinctUntilChanged()
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> folderGateway.getMostPlayed(param)
                    .distinctUntilChanged()
            else -> throw AssertionError("invalid media id $param")
        }
    }
}