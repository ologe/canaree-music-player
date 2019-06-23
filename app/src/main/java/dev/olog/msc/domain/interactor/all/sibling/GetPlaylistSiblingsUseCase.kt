package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.core.MediaId
import dev.olog.core.PlaylistConstants
import dev.olog.core.entity.track.Playlist
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetPlaylistSiblingsUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway2

) : ObservableUseCaseWithParam<List<Playlist>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Playlist>> {
        val playlistId = mediaId.categoryValue.toLong()

        if (PlaylistConstants.isAutoPlaylist(playlistId)){
            return Observable.just(listOf())
        }

        return gateway.observeAll().asObservable().map { playlists ->
            playlists.asSequence()
                    .filter { it.id != playlistId } // remove itself
                    .filter { it.size > 0 } // remove empty list
                    .toList()
        }
    }
}
