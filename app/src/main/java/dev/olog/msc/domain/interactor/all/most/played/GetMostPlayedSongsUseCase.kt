package dev.olog.msc.domain.interactor.all.most.played

import dev.olog.core.entity.track.Song
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.FolderGateway2
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetMostPlayedSongsUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val folderGateway: FolderGateway2,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway

) : ObservableUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {

        return when (mediaId.category) {
            MediaIdCategory.GENRES -> return genreGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            MediaIdCategory.PLAYLISTS -> return playlistGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            MediaIdCategory.FOLDERS -> folderGateway.observeMostPlayed(mediaId).asObservable()
                    .distinctUntilChanged()
            else -> Observable.just(listOf())
        }
    }
}