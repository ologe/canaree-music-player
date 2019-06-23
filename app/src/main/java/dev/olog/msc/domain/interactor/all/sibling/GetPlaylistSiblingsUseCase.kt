package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.core.PlaylistConstants
import dev.olog.core.entity.track.Playlist
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPlaylistSiblingsUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: PlaylistGateway

) : ObservableUseCaseWithParam<List<Playlist>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Playlist>> {
        val playlistId = mediaId.categoryValue.toLong()

        val observable = if (PlaylistConstants.isAutoPlaylist(playlistId)){
            gateway.getAllAutoPlaylists()
        } else gateway.getAll()

        return observable.map { playlists ->
            playlists.asSequence()
                    .filter { it.id != playlistId } // remove itself
                    .filter { it.size > 0 } // remove empty list
                    .toList()
        }
    }
}
