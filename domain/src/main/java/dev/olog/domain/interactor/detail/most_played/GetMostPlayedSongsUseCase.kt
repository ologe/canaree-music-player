package dev.olog.domain.interactor.detail.most_played

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Flowable
import javax.inject.Inject

class GetMostPlayedSongsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : FlowableUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Song>> {

        return when (mediaId.category) {
            MediaIdCategory.GENRE -> return genreGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            MediaIdCategory.PLAYLIST -> return playlistGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            MediaIdCategory.FOLDER -> folderGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            else -> Flowable.just(listOf())
        }
    }
}