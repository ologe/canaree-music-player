package dev.olog.msc.domain.interactor.detail.most.played

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
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